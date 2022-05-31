package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import com.codebrew.clikat.modal.agent.CblUserBean
import com.codebrew.clikat.modal.other.ProductDataBean
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AgentCustomParam(
        var agentData: CblUserBean? = null,
        var serviceDate: String? = null,
        var serviceTime: String? = null,
        var duration: Int? = null) : Parcelable {
    constructor() : this(CblUserBean(), "", "", 0)
}