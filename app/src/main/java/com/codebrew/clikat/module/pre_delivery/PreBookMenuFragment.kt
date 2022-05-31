package com.codebrew.clikat.module.pre_delivery

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.FragmentPreBookMenuBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import kotlinx.android.synthetic.main.fragment_pre_book_menu.*


class PreBookMenuFragment : AppCompatActivity() {

    private val sharedPrefFile = "kotlinsharedpreference"

    private lateinit var mBinding: FragmentPreBookMenuBinding
    lateinit var editor: SharedPreferences.Editor
    lateinit var sharedPreferences: SharedPreferences

    private var clientInform: SettingModel.DataBean.SettingData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.fragment_pre_book_menu)

        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        clReserveTable.setOnClickListener {
            editor.putString("is_table", "1")
            editor.apply()
            editor.commit()
            startActivity(Intent(this, MainScreenActivity::class.java))
        }
    }


}