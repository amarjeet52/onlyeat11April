package com.codebrew.clikat.module.signup

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.databinding.LayoutAgreeDeclarationBinding
import com.codebrew.clikat.module.signup.declaration.DeclarationDialogListener
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.layout_agree_declaration.*

class DeclarationDialog : BaseDialog() {


    private var mListener: DeclarationDialogListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<LayoutAgreeDeclarationBinding>(inflater, R.layout.layout_agree_declaration, container, false)
        binding.color = Configurations.colors
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
    }

    private fun listeners() {
        ivCross?.setOnClickListener {
            dialog?.dismiss()
        }
        btnAgree?.setOnClickListener {
            if (checkboxAgree?.isChecked == true)
            {
                dialog?.dismiss()
                mListener?.onSubmitDeclaration()
            }
            else
                AppToasty.error(requireContext(), getString(R.string.please_accept_terms))
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as DeclarationDialogListener
        } else {
            context as DeclarationDialogListener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    companion object {
        fun newInstance(): DeclarationDialog = DeclarationDialog()
    }


}
