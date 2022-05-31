package com.codebrew.clikat.app_utils.extension

import com.google.android.material.switchmaterial.SwitchMaterial

internal fun SwitchMaterial.onCheckChanged(status: (Boolean) -> Unit)
{
    this.setOnCheckedChangeListener { compoundButton, b ->
        status.invoke(b)
    }
}