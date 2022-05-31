package com.codebrew.clikat.module.new_signup.otp_verify

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.codebrew.clikat.R


class GenericTextWatcher(val view: View, private val editText: Array<EditText>) : TextWatcher {

    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        when (view.id) {
            R.id.etOtp1 -> if (text.length == 1) editText[1].requestFocus()
            R.id.etOtp2 -> if (text.length == 1) editText[2].requestFocus() else if (text.isEmpty()) editText[0].requestFocus()
            R.id.etOtp3 -> if (text.length == 1) editText[3].requestFocus() else if (text.isEmpty()) editText[1].requestFocus()
            R.id.etOtp4 -> if (text.isEmpty()) editText[2].requestFocus()
        }
    }

    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

}