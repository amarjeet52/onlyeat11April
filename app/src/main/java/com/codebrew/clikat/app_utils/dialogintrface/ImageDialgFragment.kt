package com.codebrew.clikat.app_utils.dialogintrface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codebrew.clikat.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.codebrew.clikat.intrface.ImageCallback
import kotlinx.android.synthetic.main.dialog_select_image.*


class ImageDialgFragment : BottomSheetDialogFragment(), View.OnClickListener {

    var imageCallback: ImageCallback? = null
    var enablePdfUpload: Boolean? = false

    fun settingCallback(imageCallback: ImageCallback) {
        this.imageCallback = imageCallback
    }

    fun setPdfUpload(enablePdfUpload: Boolean) {
        this.enablePdfUpload = enablePdfUpload
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.dialog_select_image, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingLayout()
    }

    private fun settingLayout() {
        btnPdf?.visibility = if (enablePdfUpload == true) View.VISIBLE else View.GONE
        btn_camera.setOnClickListener(this)
        btn_gallery.setOnClickListener(this)
        btnPdf.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_camera -> {
                imageCallback?.onCamera()
                dismiss()
            }
            R.id.btn_gallery -> {
                imageCallback?.onGallery()
                dismiss()
            }
            R.id.btnPdf->{
                imageCallback?.onPdf()
                dismiss()
            }
        }
    }

}
