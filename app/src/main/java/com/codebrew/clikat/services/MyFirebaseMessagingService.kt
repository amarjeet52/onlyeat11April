package com.codebrew.clikat.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.model.others.OrderEvent
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.order_detail.OrderDetailActivity
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.user_chat.UserChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*
import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */

    @Inject
    lateinit var dataManager: DataManager

    lateinit var vibrator: Vibrator
    var orderId: String = ""

    var senderName: String? = null

    @Override
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()

        //screenFlowBean=dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
    }

    override fun onNewToken(token: String) {
        dataManager.setkeyValue(DataNames.REGISTRATION_ID, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("notifi", remoteMessage.data.toString())
        vibrator  = getSystemService(BaseActivity.VIBRATOR_SERVICE) as Vibrator
        //{device_token=fF20iu4QQAyue_hQGfmemr:APA91bHrYnNPie-9pL3FbPT6_FHsJ1p2Onnq85hbu_2KOWIcfcr7Zil_en4aV7CFDapGzaLoDFHwOiBnNODZo-SuwhWBBda6PMAmTrpVGvtqhvWc
        // _VdCfQhp5Nx2puFjTxnyPUIY7GQu, id=59, date=1584967088626, text=Xcf, type=chat, order_id=211, chat_type=text, user_image=}
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            sendNotification(remoteMessage.data, remoteMessage.notification)
        }
    }

    private fun sendNotification(messageBody: Map<String, String?>, notification: RemoteMessage.Notification?) {
        val notificationItent: Intent
        var pendingIntent: PendingIntent? = null
        var requestID = System.currentTimeMillis().toInt()
        val homeIntent: Intent = Intent(this, MainScreenActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        if (messageBody.containsKey("type")) {
            when (messageBody["type"]) {
                "chat" -> {
                    requestID = if (messageBody.containsKey("order_id")) {
                        messageBody["order_id"]?.toInt() ?: 0
                    } else {
                        0
                    }
                    orderId = requestID.toString()

                    senderName = messageBody["sender_name"]

                    val agentData = Agent(agent_created_id = messageBody["agent_created_id"]
                            ?: "0", image = messageBody["sender_image"], name = senderName)

                    notificationItent = Intent(this, UserChatActivity::class.java)
                    notificationItent.putExtra("userData", agentData)
                    if(messageBody.containsKey("order_id"))
                    notificationItent.putExtra("orderId", messageBody["order_id"] ?: "0")
                    notificationItent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    pendingIntent = PendingIntent.getActivity(
                            this,
                            requestID,
                            notificationItent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    )

                }
            }
        } else {
            if(messageBody["status"]?.toString().equals("10"))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_HEAVY_CLICK));
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(500);
                }
            }
            if (messageBody.containsKey("orderId") &&  (messageBody["orderId"]?.toInt() ?: 0!=0)) {

                val previousCount= dataManager.getKeyValue(PrefenceConstants.BADGE_COUNT, PrefenceConstants.TYPE_STRING).toString().toIntOrNull()?:0
                previousCount.inc()

                dataManager.setkeyValue(PrefenceConstants.BADGE_COUNT, previousCount.toString())

                EventBus.getDefault().post(OrderEvent(AppConstants.NOTIFICATION_EVENT))

                val orderId = messageBody["orderId"]
                notificationItent = Intent(this, UserTracking::class.java)
                val arrayList = ArrayList<Int>()
                arrayList.add(orderId?.toInt() ?: 0)
                notificationItent.putIntegerArrayListExtra("orderId", arrayList)
                notificationItent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                pendingIntent = PendingIntent.getActivities(this, requestID, arrayOf(homeIntent, notificationItent), PendingIntent.FLAG_ONE_SHOT)
            } else {
                pendingIntent = PendingIntent.getActivity(this, requestID /* Request code */, homeIntent,
                        PendingIntent.FLAG_ONE_SHOT)
            }
        }


        //{orderId=203, message= Your Order Has been Confirmed}
        var image = ""
        var remote_picture: Bitmap? = null
        if (messageBody.containsKey("data")) {
            try {
                val `object` = JSONObject(messageBody["data"] ?: "")
                image = `object`.getString("images")
                val notiStyle = NotificationCompat.BigPictureStyle()
                try {
                    remote_picture = BitmapFactory.decodeStream(URL(image).content as InputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                notiStyle.bigPicture(remote_picture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val intent = Intent(this, MainScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val channelId = getString(R.string.default_notification_channel_id)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val VIBRATE_PATTERN = longArrayOf(0, 500)

        /* val msgText=if(notification?.body.toString().toLowerCase().contains("order"))
         {
             notification?.body.toString().toLowerCase().replace("order", statusProduct(list.get(position).getStatus(), 0, -1, mContext))
         }
         else
         {
             notification?.body.toString()
         }*/


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_new)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(notification?.body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(VIBRATE_PATTERN)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        senderName?.let {
            notificationBuilder.setContentTitle(it)
        }


        if (remote_picture != null) {
            notificationBuilder.setLargeIcon(remote_picture)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "clickat_channel",
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        if (AppConstants.isChatOpen && AppConstants.currentOrderId == orderId) {

        } else {
            notificationManager.notify(requestID /* ID of notification */, notificationBuilder.build())
        }
    }


}
