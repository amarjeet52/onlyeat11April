package com.codebrew.clikat.app_utils.extension

import android.widget.RadioGroup

internal fun RadioGroup.onCheckChange(check:(Int,RadioGroup)-> Unit)
{
    this.setOnCheckedChangeListener { group, checkedId ->
        check.invoke(checkedId,group)
    }
}