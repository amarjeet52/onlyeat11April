package com.codebrew.clikat.app_utils

import android.app.Activity
import android.os.AsyncTask
import com.codebrew.clikat.dialog_flow.DialogChat
import com.google.cloud.dialogflow.v2.*

class DialogRequest internal constructor(var activity: DialogChat, private val session: SessionName?,
                                         private val sessionsClient: SessionsClient?,
                                         private val queryInput: QueryInput) : AsyncTask<Void?, Void?, DetectIntentResponse?>() {
    override fun doInBackground(vararg p0: Void?): DetectIntentResponse? {
        try {
            val detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .build()
            return sessionsClient?.detectIntent(detectIntentRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(response: DetectIntentResponse?) {
        activity.callbackV2(response)
    }

}