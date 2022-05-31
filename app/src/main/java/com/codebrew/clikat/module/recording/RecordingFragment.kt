package com.codebrew.clikat.module.recording

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.others.ImageListModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentRecordingBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.OfferDataBean
import com.codebrew.clikat.modal.other.SuccessModel
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsViewModel
import com.codebrew.clikat.module.setting.SettingNavigator
import com.codebrew.clikat.module.setting.SettingViewModel
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import java.util.*
import javax.inject.Inject


class RecordingFragment : BaseFragment<FragmentRecordingBinding, RecordingViewModel>(),
    RecordAudioNavigator {


    val TAG = "MainActivity"

    val SAMPLE_RATE = 44100 // supported on all devices

    val CHANNEL_CONFIG_IN = AudioFormat.CHANNEL_IN_MONO
    val CHANNEL_CONFIG_OUT = AudioFormat.CHANNEL_OUT_MONO
    val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_8BIT // not supported on all devices

    val BUFFER_SIZE_RECORDING =
        AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_IN, AUDIO_FORMAT)
    val BUFFER_SIZE_PLAYING =
        AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_OUT, AUDIO_FORMAT)

    var audioRecord: AudioRecord? = null
    var audioTrack: AudioTrack? = null

    private var recordingThread: Thread? = null
    private var playingThread: Thread? = null


    var isRecordingMedia = false
    var isPlayingMedia = false
    var isRecordingAudio = false
    var isPlayingAudio = false
    private var seconds = 0

    // Is the stopwatch running?
    private var running = false

    private var wasRunning = false

    var fileNameMedia: String? = null
    var fileNameAudio: String? = null

    private lateinit var viewModel: RecordingViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var mDataManager: PreferenceHelper
    private var mBinding: FragmentRecordingBinding? = null


    override fun getViewModel(): RecordingViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RecordingViewModel::class.java)
        return viewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_recording
    }


    override fun onSosSuccess() {
        navController(this@RecordingFragment).popBackStack()

    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding

        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        recordObserver()
        runTimer()
        fileNameMedia =
            requireActivity().getFilesDir()
                .getPath() + "/testfile" + ".3gp" // store audio files in internal storage

        fileNameAudio = requireActivity().getFilesDir().getPath() + "/testfile" + ".pcm"

        val fileMedia = File(fileNameMedia)
        val fileAudio = File(fileNameAudio)
        if (!fileMedia.exists() || fileAudio.exists()) { // create empty files if needed
            try {
                fileMedia.createNewFile()
                fileAudio.createNewFile()
            } catch (e: IOException) {

                e.printStackTrace()
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) { // get permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                200
            )
        }

        setListeners()

        mBinding!!.lytToolbar.tbName.text = "Record Order"



        mBinding!!.lytToolbar.tbBack.setOnClickListener {
            stopPlaying()
            navController(this@RecordingFragment).popBackStack()
        }
        mBinding!!.tvTimings.text = "00.00"


    }


    fun callApi() {


        mBinding!!.imageLoader.visibility = View.VISIBLE
        val file = File(fileNameAudio!!)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        val mLocUser = mDataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

        if (mLocUser != null) {

            builder.addFormDataPart("latitude", mLocUser!!.latitude ?: "")
            builder.addFormDataPart("longitude", mLocUser!!.longitude ?: "")
          }
        builder.addFormDataPart("user_id",  mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString())

        builder.addFormDataPart(
            "access_token",
            "211fc38a84610eb4ba4afc8b5b48baee93b229ef9b8e81028278720cdfb2910ddf31488a2c7e4168d7a044b8ec92e0d332ac2860c22e5dc622a499c8c67d82ac0d3280b0603dad39f7d78a55beda74c5"
        )
        builder.addFormDataPart(
            "audio", file.name, file
                .asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        val requestBody = builder.build()
        viewModel.uploadAudio(requestBody)
    }


    override fun onPause() {
        super.onPause()
        wasRunning = running
        running = false
        stopPlaying()
    }


    override fun onResume() {
        super.onResume()
        if (wasRunning) {
            running = true
        }
    }

    fun onClickStart() {
        running = true
    }


    fun onClickStop() {
        running = false
    }

    fun onClickReset(view: View?) {
        running = false
        seconds = 0
    }

    private fun recordObserver() {
        val catObserver = Observer<String> { resource ->

            mBinding!!.imageLoader.visibility = View.GONE
            navController(this).popBackStack()
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.recordAudioLiveData.observe(this, catObserver)
    }

    private fun runTimer() {

        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                // Set the text view text.
                mBinding!!.tvTimings.text = time

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) { // handle user response to permission request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                requireActivity(),
                "Permission to record audio granted",
                Toast.LENGTH_LONG
            ).show()
            mBinding!!.ivRecord!!.isEnabled = true
            mBinding!!.ivStop!!.isEnabled = true
            mBinding!!.ivReset!!.isEnabled = true
        } else {
            Toast.makeText(
                requireActivity(),
                "Permission to record audio denied",
                Toast.LENGTH_LONG
            ).show()
            mBinding!!.ivRecord!!.isEnabled = false
            mBinding!!.ivStop!!.isEnabled = false
            mBinding!!.ivReset!!.isEnabled = false

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setListeners() { // start or stop recording and playback depending on state
        mBinding!!.ivRecord!!.isEnabled = true
        mBinding!!.ivStop!!.isEnabled = true
        mBinding!!.ivReset!!.isEnabled = true
        mBinding!!.ivRecord.setOnClickListener {
            if (!isRecordingAudio) {
                startRecording()
            }
            setButtonText()
        }
        mBinding!!.ivStop.setOnClickListener {
            stopRecording()
            setButtonText()
        }
        mBinding!!.tvListening.setOnClickListener {
            startPlaying()
        }
        mBinding!!.ivSendOrder.setOnClickListener {
            if (isPlayingAudio) {
                stopPlaying()
            }
            callApi()
        }
        mBinding!!.ivReset.setOnClickListener {
            mBinding!!.ivRecord.visibility = View.VISIBLE
            mBinding!!.ivStop.visibility = View.GONE
            mBinding!!.ivReset.visibility = View.GONE
            mBinding!!.ivSound.visibility = View.GONE
            mBinding!!.tvListening.visibility = View.GONE
            mBinding!!.tvROrder.visibility = View.VISIBLE
            mBinding!!.tvTimings.visibility = View.VISIBLE
            mBinding!!.ivSendOrder.visibility = View.GONE
            seconds = 0
            mBinding!!.tvROrder.setText(getString(R.string.record_order))
            mBinding!!.tvTimings.setText("00:00")
            if (isPlayingAudio) {
                stopPlaying()
            }
            if (isRecordingAudio) {
                stopRecording()
            }
        }
    }

    private fun startRecording() {
        if (audioRecord == null) { // safety check
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            onClickStart()
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG_IN,
                AUDIO_FORMAT,
                BUFFER_SIZE_RECORDING
            )
            if (audioRecord!!.state != AudioRecord.STATE_INITIALIZED) { // check for proper initialization
                Toast.makeText(
                    requireActivity(),
                    "Couldn't initialize AudioRecord, check configuration",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            audioRecord!!.startRecording()

            isRecordingAudio = true
            recordingThread = Thread { writeAudioDataToFile() }
            recordingThread!!.start()
        }
//        }
    }

    private fun stopRecording() {
        if (audioRecord != null) {
            onClickStop()
            isRecordingAudio = false // triggers recordingThread to exit while loop
        }

//        }
    }

    private fun startPlaying() {
        if (audioTrack == null) {
            audioTrack = AudioTrack(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build(),
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
                BUFFER_SIZE_PLAYING,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            if (audioTrack!!.state != AudioTrack.STATE_INITIALIZED) {
                Toast.makeText(
                    requireActivity(),
                    "Couldn't initialize AudioTrack, check configuration",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
            audioTrack!!.play()

            isPlayingAudio = true
            playingThread = Thread { readAudioDataFromFile() }
            playingThread!!.start()
        }
//        }
    }

    private fun stopPlaying() {
//        if (id == R.id.play_mediaplayer) {
//            if (mediaPlayer != null) {
//                mediaPlayer!!.release()
//                mediaPlayer = null
//            }
//        } else {
        isPlayingAudio = false // will trigger playingThread to exit while loop
//        }
    }

    private fun writeAudioDataToFile() { // called inside Runnable of recordingThread
        val data =
            ByteArray(BUFFER_SIZE_RECORDING / 2) // assign size so that bytes are read in in chunks inferior to AudioRecord internal buffer size
        var outputStream: FileOutputStream? = null
        outputStream = try {
            FileOutputStream(fileNameAudio)
        } catch (e: FileNotFoundException) {
            // handle error

            return
        }
        while (isRecordingAudio) {
            val read = audioRecord!!.read(data, 0, data.size)
            try {
                outputStream!!.write(data, 0, read)
            } catch (e: IOException) {

                e.printStackTrace()
            }
        }
        try { // clean up file writing operations
            outputStream!!.flush()
            outputStream!!.close()
        } catch (e: IOException) {

            e.printStackTrace()
        }
        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null
        recordingThread = null
    }

    private fun readAudioDataFromFile() { // called inside Runnable of playingThread
        var fileInputStream: FileInputStream? = null
        fileInputStream = try {
            FileInputStream(fileNameAudio)
        } catch (e: IOException) {

            e.printStackTrace()
            return
        }
        val data = ByteArray(BUFFER_SIZE_PLAYING / 2)
        var i = 0
        while (isPlayingAudio && i != -1) { // continue until run out of data or user stops playback
            try {
                i = fileInputStream!!.read(data)
                audioTrack!!.write(data, 0, i)
            } catch (e: IOException) {

                e.printStackTrace()
                return
            }
        }
        try { // finish file operations
            fileInputStream!!.close()
        } catch (e: IOException) {

            e.printStackTrace()
            return
        }

        // clean up resources
        isPlayingAudio = false
        setButtonText()
        audioTrack!!.stop()
        audioTrack!!.release()
        audioTrack = null
        playingThread = null
    }

    private fun setButtonText() { // UI updates for button text

        if (isRecordingAudio) {
            mBinding!!.tvROrder.text = getString(R.string.recording)
            mBinding!!.ivStop.visibility = View.VISIBLE
            mBinding!!.ivReset.visibility = View.VISIBLE
            mBinding!!.ivRecord.visibility = View.GONE
        } else {
            mBinding!!.ivStop.visibility = View.GONE
            mBinding!!.ivReset.visibility = View.VISIBLE
            mBinding!!.ivRecord.visibility = View.GONE
            mBinding!!.ivSendOrder.visibility = View.VISIBLE
            mBinding!!.ivSound.visibility = View.VISIBLE
            mBinding!!.tvListening.visibility = View.VISIBLE
            mBinding!!.tvTimings.visibility = View.GONE
            mBinding!!.tvROrder.visibility = View.GONE
        }

    }

}