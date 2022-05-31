package com.codebrew.clikat.module.restaurant_detail

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.codebrew.clikat.R
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayer : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        setListeners()
        setData()
    }

    private fun setData() {
        try {
            val uri = Uri.parse(intent.getStringExtra("link"))
            mVideoView.setVideoURI(uri)
            mVideoView.canPause()
            val mediaController = MediaController(this)
            mVideoView.setMediaController(mediaController)

            mVideoView.setZOrderOnTop(true)
            mVideoView.start()
            mVideoView.setOnCompletionListener { mediaPlayer ->
                mediaPlayer.stop()
                mVideoView.stopPlayback()
                finish()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            mVideoView.layoutParams = layoutParams
        } else {
            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            mVideoView.layoutParams = layoutParams
        }
    }
}