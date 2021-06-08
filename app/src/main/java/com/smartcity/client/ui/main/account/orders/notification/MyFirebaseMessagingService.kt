package com.smartcity.client.ui.main.account.orders.notification

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "AppDebug"
    private var notificationUtils: NotificationUtils? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FCM Token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleCustomDataMessage(
            title = remoteMessage.data.get("title")!!,
            message = remoteMessage.data.get("message")!!
        )
    }

    private fun handleCustomDataMessage(title: String, message: String) {
        showNotificationMessage(
            this,
            title,
            message,
            false,
            true
        )
    }

    private fun showNotificationMessage(
        context: Context,
        title: String,
        message: String,
        shouldSound:Boolean,
        shouldVibrate:Boolean
    ) {
        notificationUtils = NotificationUtils(context)
        notificationUtils?.showNotificationMessage(title, message,shouldSound,shouldVibrate,true)
    }
}