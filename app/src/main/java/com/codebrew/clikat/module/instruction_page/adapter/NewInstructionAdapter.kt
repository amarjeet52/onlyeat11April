package com.codebrew.clikat.module.instruction_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.databinding.PagerInstructionNewBinding
import com.codebrew.clikat.modal.other.InstructionDtaModel
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.instruction_page.InstructionModel
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.pager_instruction_new.view.*

class NewInstructionAdapter(
        private val appUtils: AppUtils,
        private val tutorialScreens: ArrayList<InstructionModel>//SettingModel.DataBean.TutorialItem>,
) : RecyclerView.Adapter<NewInstructionAdapter.View_holder>() {

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var mCallback: InstructionCallback? = null
    private lateinit var mContext: Context

    // private var mContext:Context?=null
    fun settingCallback(mCallback: InstructionCallback?) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewInstructionAdapter.View_holder {
        val binding: PagerInstructionNewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pager_instruction_new, parent, false)
        binding.color = Configurations.colors
        binding.strings = textConfig

        mContext = parent.context

        return View_holder(binding.root)
    }

    override fun getItemCount(): Int {
        return tutorialScreens.count()
    }

    override fun onBindViewHolder(holder: NewInstructionAdapter.View_holder, position: Int) {

        holder.ivInstruction?.let { Glide.with(mContext).load(tutorialScreens[position].tutorial_image).into(it) }

        if (position == 1) {
            holder.tvTitle?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
        }else if(position==3) {
            holder.tvTitle?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
        }else {
            holder.tvTitle?.setTextColor(ContextCompat.getColor(mContext, R.color.progressBarColor))
        }
        holder.tvTitle?.text = tutorialScreens[position].tutorial_title
        holder.tvBody?.text = tutorialScreens[position].tutorial_text
    }


    interface InstructionCallback {
        fun onNextButton(position: Int)
    }

    inner class View_holder(root: View) : RecyclerView.ViewHolder(root) {

        var ivInstruction: ImageView? = root.iv_instruction
        var tvTitle: TextView? = root.tv_title
        var tvBody: TextView? = root.tv_body
    }

}