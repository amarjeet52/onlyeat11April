package com.codebrew.clikat.module.payment_gateway.addCard

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

class FourDigitCardFormatWatcher(private var Edt: EditText, private var list: ArrayList<String>, private var cardInterface: CardInterface) : TextWatcher {
    private var value = ""
    private var keyDel = 0

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        /*Edt.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL)
                keyDel = 1
            false
        })

        if (keyDel == 0) {
            val len = Edt.getText().length
            val input = Edt.getText().toString()
            Log.e("TAG", " length : $len")
            if (len == 4) {
                Edt.setText(input.substring(0, 4) + "" + input.substring(4))
                Edt.setSelection(Edt.getText().length)
            }

            if (len == 10) {
                Edt.setText(input.substring(0, 9) + " "+ input.substring(9))
                Edt.setSelection(Edt.getText().length)
            }

            if (len == 15) {
                Edt.setText(input.substring(0, 14) + "" + input.substring(14))
                Edt.setSelection(Edt.getText().length)
            }
        } else if (keyDel == 1) {
            val len = Edt.getText().length
            if (len == 4 || len == 10 || len == 15) {
                Edt.setText(Edt.getText().delete(Edt.getText().length - 1, Edt.getText().length))
                Edt.setSelection(Edt.getText().length)
            }
            keyDel = 0
        }*/
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {
        value = s.toString()
        val ccNum = value.replace(" ", "")
        for (i in list.indices) {
            if (ccNum.matches(list[i].toRegex())) {
                Log.e("DEBUG", "afterTextChanged : discover + : " + i)
                cardInterface.getSelectedCardData(i)
                break
            } else {
                cardInterface.getSelectedCardData(10)
            }
        }
    }

    companion object {
        private val space = ' '
    }
}

interface CardInterface {
    fun getSelectedCardData(position: Int)
}