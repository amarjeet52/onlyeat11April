package com.codebrew.clikat.data.model.api.chat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class Image(

        @field:SerializedName("thumbnail")
        var thumbnail: String? = null,

        @field:SerializedName("original")
        var original: String? = null
):Parcelable

data class SampledImage(val original: File,
                        val thumbnail: File)