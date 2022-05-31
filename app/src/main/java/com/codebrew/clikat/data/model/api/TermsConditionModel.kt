package com.codebrew.clikat.data.model.api


import androidx.annotation.Keep

@Keep
data class TermsConditionModel(
    val data: List<Data?>?,
    val message: String?,
    val status: Int?
)


@Keep
data class Data(
        val about_us: String?,
        val faq: String?,
        val id: Int?,
        val language_id: Int?,
        val terms_and_conditions: String?,
        val faqs:String?
)