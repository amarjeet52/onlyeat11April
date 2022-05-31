package com.codebrew.clikat.data.model.api.distance_matrix


import androidx.annotation.Keep

@Keep
data class Element(
    val distance: Distance?,
    val duration: Duration?,
    val status: String?
)