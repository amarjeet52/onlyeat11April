package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import com.codebrew.clikat.modal.other.SettingModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomPayModel(var payName: String? = null, val image: Int? = null,
                          var keyId: String? = null,
                          var payment_token: String? = null,
                          val payement_front: List<SettingModel.DataBean.KeyValueFront?>? = null,
                          val addCard: Boolean? = false,
                          var cardId: String? = null,
                          var customerId: String? = null,
                          var mumybenePay: String? = null,
                          var isSelected: Boolean? = false,
                          var walletAmount: Float? = 0f,
                          var showAddMoney: Boolean = false,
                          var authnet_payment_profile_id: String? = null,
                          var authnet_profile_id: String? = null,
                          var payment_name: String? = null,
                          var token: String? = null) : Parcelable