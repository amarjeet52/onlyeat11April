package com.codebrew.clikat.module.crave_mania

data class CraveManiaBannerModel(
    val data: List<BannerData>,
    val message: String,
    val status: Int
)
data class BannerData(
        val created_at: String,
        val deleted_by: Int,
        val for_front_end: Int,
        val id: Int,
        val is_selection: Int,
        val key: String,
        val updated_at: String,
        val value: String
)