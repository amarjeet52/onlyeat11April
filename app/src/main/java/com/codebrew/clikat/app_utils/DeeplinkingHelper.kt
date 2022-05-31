package com.codebrew.clikat.app_utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink


object DeeplinkingHelper {
   // val deeplinkPrefix = "betrnoms.page.link"
    val deeplinkPrefix = "royofoodmulti.page.link"
    fun createShortLink(context: Context, uri: Uri) {
        var shortLink: String? = null

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(uri)
                .setDomainUriPrefix("https://$deeplinkPrefix")
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        shortLink = task.result?.shortLink.toString()
                        val flowchartLink = task.result?.previewLink
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "text/plain"
                        share.putExtra(Intent.EXTRA_TEXT, shortLink)
                        context.startActivity(Intent.createChooser(share, "Share"))

                    } else {
                            AppToasty.error(context, "Configuration pending at console")
                    }
                }

    }

    @SuppressLint("NewApi")
    fun getNeededUri(appendPath: String?, map: HashMap<String, String>): Uri {
        val builder = Uri.Builder()
        builder.scheme("https")
                .authority(deeplinkPrefix)
                .appendPath(appendPath)
        map.forEach { (key, value) ->
            builder.appendQueryParameter(key, value)
        }

        return builder.build()
    }


    fun createDeeplink(uri: Uri): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(uri)
                .setDomainUriPrefix("https://$deeplinkPrefix") // Open links with this app on Android
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build()) // Open links with com.example.ios on iOS
                .setIosParameters(DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink()

        return dynamicLink.uri
    }

}