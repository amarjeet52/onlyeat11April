package com.codebrew.clikat.user_chat

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.codebrew.agentapp.utils.ImageSampling
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.activities.ImageSHow
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.ImageUtility
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.SocketManager
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.MessageStatus
import com.codebrew.clikat.data.ReceiverType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.ChatMessageListing
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityUserChatBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.user_chat.adapter.ChatAdapter
import com.codebrew.clikat.user_chat.adapter.ChatListener
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.codebrew.clikat.intrface.ImageCallback
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import io.socket.client.Ack
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_user_chat.*
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserChatActivity() : BaseActivity<ActivityUserChatBinding, UserChatViewModel>(),
        UserChatNavigator, EasyPermissions.PermissionCallbacks, ImageCallback, Parcelable {

    private var mUserChatModel: UserChatViewModel? = null
    private lateinit var mActivityUserChatBinding: ActivityUserChatBinding

    private var orderId = ""
    private var userDetail: Agent? = null
    private var receiverType: String? = ""
    private lateinit var adapter: ChatAdapter

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDataManager: PreferenceHelper

    @Inject
    lateinit var mDateTimeUtils: DateTimeUtils

    @Inject
    lateinit var permissionFile: PermissionFile

    var mLinearLayoutManager: LinearLayoutManager? = null

    private val imageCacheDirectory by lazy { application.externalCacheDir?.absolutePath }

    private var imageFile: File? = null

    @Inject
    lateinit var imageUtils: ImageUtility
    private val imageDialog by lazy { ImageDialgFragment() }

    val gson = Gson()

    private var signUp: PojoSignUp? = null

    private val socketManager
            by lazy {
                SocketManager.getInstance(mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(),
                        mDataManager.getKeyValue(PrefenceConstants.DB_SECRET, PrefenceConstants.TYPE_STRING).toString())
            }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_chat
    }

    override fun getViewModel(): UserChatViewModel {
        mUserChatModel = ViewModelProviders.of(this, factory).get(UserChatViewModel::class.java)
        return mUserChatModel as UserChatViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityUserChatBinding = viewDataBinding
        mActivityUserChatBinding.color = Configurations.colors
        mActivityUserChatBinding.drawables = Configurations.drawables
        mActivityUserChatBinding.strings = Configurations.strings
        mUserChatModel?.navigator = this

        signUp = mDataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

        initialise()
        chatObserver()

        if (intent != null) {
            if (intent.hasExtra("orderId")) {
                orderId = intent.getStringExtra("orderId") ?: ""
            }

            if (intent.hasExtra("userData")) {
                userDetail = intent.getParcelableExtra("userData")
                userDetail?.message_id = ""
            }

            if (intent.hasExtra("userType"))
                receiverType = intent.getStringExtra("userType") ?: ""
        }

        settingToolbar()

        AppConstants.isChatOpen = true
        AppConstants.currentOrderId = orderId
        initialiseSocket()
        checkMessageIdForAdmin()


        setupChatRecycler()
        listeners()
        btnAttachment?.visibility = if (receiverType != null && receiverType == ReceiverType.ADMIN.type) View.GONE else View.VISIBLE
    }

    private fun checkMessageIdForAdmin() {
//        if (!receiverType.isNullOrEmpty() && receiverType == ReceiverType.ADMIN.type && userDetail?.message_id.isNullOrEmpty()) {
//            if (isNetworkConnected)
//                viewModel.apiCheckMessageId("Admin", mDataManager.getKeyValue(PrefenceConstants.USER_CHAT_ID, PrefenceConstants.TYPE_STRING)?.toString()
//                        ?: "")
//        } else
            callApi(true)
    }

    private fun emitJoinRoomSocket() {
        if (!receiverType.isNullOrEmpty() && receiverType == ReceiverType.ADMIN.type && isNetworkConnected) {
            reconnectToSocket()
            val userInfo = mDataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

            val jsonObject = JSONObject()
            jsonObject.put("message_id", userDetail?.message_id ?: "")
            jsonObject.put("username", userInfo?.data?.firstname ?: "")

            /* val joinRoom=JSONObject()
             joinRoom.put("data", jsonObject)*/
            socketManager.emit(
                    SocketManager.JOIN_ROOM,
                    jsonObject,
                    Ack {
                        val acknowledgement = it.firstOrNull()
                        if (acknowledgement != null && acknowledgement is JSONObject) {
                            val response = Gson().fromJson<ChatMessageListing>(
                                    acknowledgement.toString(),
                                    object : TypeToken<ChatMessageListing>() {}.type
                            )
                            //  sendMessage.value = response
                            //  navigator.onSendMessage(response)
                        }
                    })
            socketManager.onErrorEvent()
        }
    }

    private fun reconnectToSocket() {
        if (!socketManager.checkConnection()) {
            socketManager.connect(socketListener)
        }

    }

    private fun listeners() {
        fabSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotBlank() && isNetworkConnected) {
                sendMessage(message, null, AppConstants.MESSAGE_TYPE_TEXT)
            }
        }
        iconBack?.setOnClickListener {
            onBackPressed()
        }

        btnAttachment?.setOnClickListener {
            if (permissionFile.hasCameraPermissions(this)) {
                if (isNetworkConnected) {
                    showImagePicker()
                }
            } else {
                permissionFile.cameraAndGalleryActivity(this)
            }
        }
    }

    private fun initialise() {
        mLinearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mLinearLayoutManager?.stackFromEnd = true
        rv_user_chat.layoutManager = mLinearLayoutManager
        adapter = ChatAdapter(ChatListener({

        }, {

        },{ model: ChatMessageListing, imageView: ImageView ->
            val options = ActivityOptions.makeSceneTransitionAnimation(this, imageView, "postImage")
            val intent = Intent(this, ImageSHow::class.java)
            intent.putExtra("image", if (model.localFile != null) {
                model.localFile?.absolutePath?.toString()?:""
            } else {
                model.image_url
            })
            startActivity(intent, options.toBundle())
        }))
        rv_user_chat.adapter = adapter
    }

    private fun initialiseSocket() {
        if (isNetworkConnected) {
            socketManager.on(SocketManager.ON_RECEIVE_MESSAGE, recieveChatListener)
            socketManager.connect(socketListener)
        }
    }

    override fun onPause() {
        super.onPause()

        AppConstants.currentOrderId = ""
        AppConstants.isChatOpen = false
    }


    private fun settingToolbar() {
        userDetail?.image?.let { ivProfilePic.loadImage(it) }
        tvTitle.text = userDetail?.name
        iconBack.setOnClickListener {
            finish()
        }
    }


    private fun callApi(isFirst: Boolean) {
        if (isNetworkConnected) {
            viewModel.fetchAllChat(orderId, isFirst, userDetail, receiverType)
        }
    }

    private fun setupChatRecycler() {
        (rv_user_chat.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        rv_user_chat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rv_user_chat.canScrollVertically(-1) && viewModel.validForPaging()) {
                    callApi(false)
                }
            }
        })
    }

    private fun chatObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<List<ChatMessageListing>>> { resource ->
            emitJoinRoomSocket()
            if (resource?.result?.isNotEmpty() == true) {
                val messagesList = ArrayList<ChatMessageListing>()
                resource.result.map { messageResult ->
                    if (messageResult.send_by == mDataManager.getKeyValue(PrefenceConstants.USER_CHAT_ID, PrefenceConstants.TYPE_STRING)) {
                        messageResult.ownMessage = true
                    }
                    messageResult.sent_at = mDateTimeUtils.convertDateOneToAnother(messageResult.sent_at
                            ?: "", "yyyy-MM-dd HH:mm:ss", "hh:mm aaa")

                }
                resource.let { messagesList.addAll(it.result ?: emptyList()) }
                messagesList.reverse()
                adapter.addItmSubmitList(messagesList)

                if (resource.isFirstPage)
                    rv_user_chat.smoothScrollToPosition(adapter.itemCount.minus(1))
            }
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.userMessagelist.observe(this, catObserver)
    }


    private fun updateMessage(resource: ChatMessageListing?) {

        if (resource?.send_by == mDataManager.getKeyValue(PrefenceConstants.USER_CHAT_ID, PrefenceConstants.TYPE_STRING)) {
            resource?.ownMessage = true
        }
        resource?.sent_at = mDateTimeUtils.convertDateOneToAnother(resource?.sent_at
                ?: "", "yyyy-MM-dd HH:mm:ss", "hh:mm aaa")

        if (resource != null) {
            adapter.addNewMessage(resource)
        }
        viewModel.setListCount(adapter.itemCount)
        rv_user_chat.smoothScrollToPosition(adapter.itemCount - 1)
    }


    private fun sendMessage(message: String, file: File?, type: String) {

        if (isNetworkConnected) {
            etMessage.setText("")

            val messageModel = ChatMessageListing("",
                    "",
                    orderId,
                    userDetail?.agent_created_id,
                    mDataManager.getKeyValue(PrefenceConstants.USER_CHAT_ID, PrefenceConstants.TYPE_STRING).toString(),
                    message,
                    "",
                    mDateTimeUtils.currentDate,
                    type,
                    false,
                    "",
                    "",
                    true,
                    "",
                    "",
                    receiverType)

            if (type == AppConstants.MESSAGE_TYPE_IMAGE) {
                messageModel.localFile = file
                messageModel.localId = UUID.randomUUID().toString()
                messageModel.messageStatus = MessageStatus.SENDING
                sendImageMessageWithResize(messageModel)
            } else {
                sendMessage(messageModel, mDateTimeUtils, userDetail?.agent_created_id
                        ?: "", receiverType)
                updateMessage(messageModel)
            }
        }
    }

    private fun sendImageMessageWithResize(chatMessage: ChatMessageListing) {
        if (chatMessage.chat_type != AppConstants.MESSAGE_TYPE_IMAGE) {
            Timber.w("Chat message type is not image. Skipping resizing.")
            return
        }


        val localImage = chatMessage.localFile
        if (localImage != null) {
            val imageSampling = ImageSampling(ImageSampling.OnImageSampledListener { sampledImage ->
                chatMessage.localFile = sampledImage.original
                chatMessage.localFileThumbnail = sampledImage.thumbnail

                updateMessage(chatMessage)
                viewModel.apiUploadImage(chatMessage)
            })
            imageSampling.sampleImage(localImage.absolutePath, imageCacheDirectory ?: "", 800)
        }
    }


    override fun onErrorOccur(message: String) {
        window.decorView.rootView.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onImageUploaded(newMessage: ChatMessageListing?) {
        adapter.updateMessageStatus(newMessage?.localId ?: "", MessageStatus.SENT)
        sendMessage(newMessage, mDateTimeUtils, userDetail?.agent_created_id
                ?: "", receiverType)
    }

    override fun onChatMessageId(messageId: String) {
        val userInfo = mDataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        userInfo?.data?.message_id = messageId
        mDataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(userInfo))
        userDetail?.message_id = messageId
        callApi(true)
    }


    private fun sendMessage(messageModel: ChatMessageListing?, mDateTime: DateTimeUtils, userId: String, receiverType: String?) {
        // setIsLoading(true)
        if (!socketManager.checkConnection())
            socketManager.connect(socketListener)

        socketManager.emit(
                SocketManager.EMIT_SEND_MESSAGE,
                sendMsgJsonObject(messageModel, mDateTime, userId, receiverType),
                Ack {
                    val acknowledgement = it.firstOrNull()
                    if (acknowledgement != null && acknowledgement is JSONObject) {

                        //  sendMessage.value = response
                        //  navigator.onSendMessage(response)
                        /*val response = gson.fromJson(acknowledgement.toString(), SendMsgModel::class.java)

                        when (response.status) {
                            200 -> {
                                messageModel.isFailed=false
                                updateMessage(messageModel)
                            }
                            else -> {
                                messageModel.isFailed=true
                                updateMessage(messageModel)
                            }
                        }*/
                    }
                })

        socketManager.onErrorEvent()
    }

    private fun sendMsgJsonObject(message: ChatMessageListing?, mDateTime: DateTimeUtils, userId: String,
                                  receiverType: String?): JSONObject {
        val jsonObject = JSONObject()
        try {
            val obj = JSONObject()
            obj.put("receiver_created_id", userId)
            obj.put("sent_at", mDateTime.currentDate)
            obj.put("chat_type", message?.chat_type)
            obj.put("offset", mDateTimeUtils.getTimeOffset())
            obj.put("sender_image", signUp?.data?.user_image)

            obj.put("sender_created_id", mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString())

            if (!receiverType.isNullOrEmpty())
                obj.put("type", receiverType)
            else
                obj.put("type", message?.type)


            if (message?.chat_type == AppConstants.MESSAGE_TYPE_IMAGE)
                obj.put("image_url", message.image_url)

            if (message?.chat_type == AppConstants.MESSAGE_TYPE_TEXT)
                obj.put("text", message.text)


            if (message?.order_id?.isNotEmpty() == true)
                obj.put("order_id", message.order_id)

            if (!receiverType.isNullOrEmpty() && receiverType == ReceiverType.ADMIN.type)
                obj.put("message_id", userDetail?.message_id ?: "")
            else
                obj.put("message_id", "0")

            jsonObject.put("detail", obj)

            println("sentMessage -> $jsonObject")

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonObject
    }


    private val recieveChatListener = Emitter.Listener { args ->
        if (args.isNotEmpty() && args[0] is JSONObject) {
            val response = Gson().fromJson<ChatMessageListing>(
                    (args[0] as JSONObject).getJSONObject("detail").toString(),
                    object : TypeToken<ChatMessageListing>() {}.type
            )
            runOnUiThread {
                if (receiverType != null && receiverType == ReceiverType.SUPPLIER.type) {
                    if(userDetail?.user_created_id==response.sent_by)
                        updateMessage(response)
                } else
                    updateMessage(response)
            }
        }
    }


    private val socketListener = Emitter.Listener { args ->
        if (args.isNotEmpty() && args[0] is JSONObject) {
            val response = Gson().fromJson<SuccessModel>(
                    args[0].toString(),
                    object : TypeToken<SuccessModel>() {}.type
            )

            if (response?.success == NetworkConstants.AUTHFAILED) {
                mDataManager.logout()
                onSessionExpire()
            } else {
                Timber.e(response.message)
            }
        }
    }

    constructor(parcel: Parcel) : this() {
        orderId = parcel.readString()?:""
        userDetail = parcel.readParcelable(Agent::class.java.classLoader)
        receiverType = parcel.readString()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.CameraGalleryPicker) {
            if (isNetworkConnected) {
                showImagePicker()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {
            if (isNetworkConnected) {
                if (imageFile?.isRooted == true) {
                    if (isNetworkConnected) {
                        sendMessage("", imageFile, AppConstants.MESSAGE_TYPE_IMAGE)
                    }
                }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.GalleyPicker) {
            if (data != null) {
                if (isNetworkConnected) {
                    //data.getData return the content URI for the selected Image
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    // Get the cursor
                    val cursor = contentResolver?.query(selectedImage!!, filePathColumn, null, null, null)
                    // Move to first row
                    cursor?.moveToFirst()
                    //Get the column index of MediaStore.Images.Media.DATA
                    val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                    //Gets the String value in the column
                    val imgDecodableString = cursor?.getString(columnIndex ?: 0)
                    cursor?.close()

                    if (imgDecodableString?.isNotEmpty() == true) {
                        val image = imageUtils.compressImage(imgDecodableString)
                        if (isNetworkConnected) {
                            sendMessage("", File(image), AppConstants.MESSAGE_TYPE_IMAGE)
                        }
                    }
                }
            }
        }
    }

    private fun showImagePicker() {
        imageDialog.settingCallback(this)
        imageDialog.show(
                supportFragmentManager,
                "image_picker"
        )
    }

    override fun onPdf() {

    }

    override fun onGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, AppConstants.GalleyPicker)
    }

    override fun onCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager!!)?.also {
                // Create the File where the photo should go
                imageFile = try {
                    ImageUtility.filename(imageUtils)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                imageFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            packageName ?: "",
                            it)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, AppConstants.CameraPicker)
                }
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeParcelable(userDetail, flags)
        parcel.writeString(receiverType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserChatActivity> {
        override fun createFromParcel(parcel: Parcel): UserChatActivity {
            return UserChatActivity(parcel)
        }

        override fun newArray(size: Int): Array<UserChatActivity?> {
            return arrayOfNulls(size)
        }
    }

}
