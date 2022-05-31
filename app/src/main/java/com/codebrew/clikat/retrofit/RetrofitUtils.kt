package com.codebrew.clikat.retrofit

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/*
 * Created by Ankit jindal on 19/12/2015.
 */
object RetrofitUtils {
    fun textToRequestBody(text: String): RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), text)

    fun imageToRequestBody(imageFile: File): RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)

    fun imageToRequestBodyKey(parameterName: String, fileName: String): String =
            "$parameterName\"; filename=\"$fileName"


}