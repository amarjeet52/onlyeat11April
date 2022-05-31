package com.codebrew.clikat.app_utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.data.model.api.PostItem
import com.codebrew.clikat.databinding.*
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_cart.*


/**
 * contains Dialogs to be used in the application
 */
class DialogsUtil {

    fun openAlertDialog(context: Context, message: String, positiveBtnText: String, negativeBtnText: String, listener: DialogListener?) {


        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton(positiveBtnText) { dialog, which ->
            dialog.dismiss()
            listener?.onSucessListner()

        }

        if (negativeBtnText.isNotEmpty()) {
            builder.setNegativeButton(negativeBtnText) { dialog, which ->
                dialog.dismiss()
                listener?.onErrorListener()

            }
        }



        builder.setMessage(message)
        builder.setCancelable(false)
        builder.create().show()
    }


    fun showDialog(context: Context, layout: View): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(layout)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER

        dialog.window!!.attributes = lp
        dialog.setCancelable(true)
        return dialog
    }

    fun showDialogFix(context: Context, layout: View): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(layout)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER

       // dialog.window!!.attributes = lp
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog?.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

     fun showProductDialog(context: Context, productBean: ProductDataBean?) {

        val binding = DataBindingUtil.inflate<DialogProductDetailsBinding>(LayoutInflater.from(context), R.layout.dialog_product_details, null, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings

        val mDialog = showDialog(context, binding.root)
        mDialog.show()

        val productImage = mDialog.findViewById<View>(R.id.productPhoto) as ImageView
        val productTitle = mDialog.findViewById<View>(R.id.productTitle) as TextView
        val tvDesc = mDialog.findViewById<View>(R.id.tvDesc) as TextView

        productImage.loadImage(productBean?.image_path.toString() ?: "")
        productTitle.text = Utils.getHtmlData(productBean?.name?:"")
        tvDesc.text = Utils.getHtmlData(productBean?.product_desc?:"")

    }


    fun showMumyBenePhone(context: Context,phoneNumber:String,payName:String,phoneChanged: (String) -> Unit={}):String
    {

        val binding = DataBindingUtil.inflate<DialogPhoneNumberBinding>(LayoutInflater.from(context), R.layout.dialog_phone_number, null, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings

        val mDialog = showDialogFix(context, binding.root)
        mDialog.show()

        val ivCross = mDialog.findViewById<ImageView>(R.id.ivCross)
        val tvPhone = mDialog.findViewById<TextView>(R.id.tvPhone)
        val editText = mDialog.findViewById<EditText>(R.id.etPhoneNumber)
        val tvSubmit = mDialog.findViewById<TextView>(R.id.tvSubmit)
        editText?.setText(phoneNumber ?: "")
        tvPhone?.text = context.getString(R.string.enter_your_mmy_bean_phone,payName)
        tvSubmit?.setOnClickListener {
            if (editText?.text.toString().isNotEmpty())
            {
                mDialog.dismiss()}
            else {
                AppToasty.error(context, context.getString(R.string.enter_phone))
            }
        }
        ivCross?.setOnClickListener {
            mDialog.dismiss()
        }

        mDialog.setOnDismissListener {
            phoneChanged.invoke(editText?.text.toString())
        }

        mDialog.show()

        return editText?.text.toString()
    }


     fun openDialogMumybene(context: Context,cancelListener: (View) -> Unit): Dialog {
        val binding = DataBindingUtil.inflate<DialogMumybeneLoaderBinding>(LayoutInflater.from(context), R.layout.dialog_mumybene_loader, null, false)
         binding.color = Configurations.colors
         binding.strings = Configurations.strings


         val mDialog = showDialogFix(context, binding.root)

        val ivImage = mDialog.findViewById<ImageView>(R.id.ivImage)
        val btnCancel = mDialog.findViewById<MaterialButton>(R.id.tvCancel)


        btnCancel?.setOnClickListener {
            AppToasty.error(context, context.getString(R.string.request_cancelled))
            cancelListener.invoke(it)
            mDialog.dismiss()
        }

        Glide.with(context).load(R.raw.loadergif).into(ivImage)

        return mDialog

    }


    fun showReportPost(context: Context,viewModel: SocialPostViewModel):Dialog
    {

        val binding = DataBindingUtil.inflate<DialogReportPostBinding>(LayoutInflater.from(context), R.layout.dialog_report_post, null, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings
        binding.viewModel = viewModel

        val mDialog = showDialogFix(context, binding.root)
        mDialog.show()

        val ivCross = mDialog.findViewById<ImageView>(R.id.ivCross)
        ivCross?.setOnClickListener {
            mDialog.dismiss()
        }

        mDialog.show()

        return mDialog
    }

}