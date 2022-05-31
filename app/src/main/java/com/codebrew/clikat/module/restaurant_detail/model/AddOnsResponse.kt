package com.codebrew.clikat.module.restaurant_detail.model

import com.codebrew.clikat.data.model.api.AddsOn

data class AddOnsResponse(
    val `data`: List<Dataa>,
    val message: String,
    val status: Int
)
data class Dataa(
        val adds_on: List<AddsOn>,
        val price_type: Int
)