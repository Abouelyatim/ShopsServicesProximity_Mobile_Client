package com.smartcity.client.ui.main.account.orders.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smartcity.client.models.NotificationType
import com.smartcity.client.util.PreferenceKeys

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "AppDebug"
    private var notificationUtils: NotificationUtils? = null


    object Events {
        val flashBadgeEvent: MutableLiveData<Boolean> by lazy {
            MutableLiveData<Boolean>()
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FCM Token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleCustomDataMessage(
            title = remoteMessage.data.get("title")!!,
            message = remoteMessage.data.get("message")!!,
            type = NotificationType.valueOf(remoteMessage.data.get("type")!!)
        )
    }

    private fun setLastNotification(editor:SharedPreferences.Editor,title: String,message: String){
        editor.putString(PreferenceKeys.LAST_FLASH_NOTIFICATION,title+message)
        editor.apply()
    }

    private fun showNotificationFlash(editor:SharedPreferences.Editor,title: String,message: String){
        showNotificationMessage(
            this,
            title,
            message,
            false,
            true
        )
        setLastNotification(editor,title,message)
        setNewFlashNotification(editor)
    }

    private fun setNewFlashNotification(editor:SharedPreferences.Editor){
        Events.flashBadgeEvent.postValue(true)
        editor.putBoolean(PreferenceKeys.NEW_FLASH_NOTIFICATION,true)
        editor.apply()
    }

    @SuppressLint("CommitPrefEdits")
    private fun handleCustomDataMessage(title: String, message: String, type: NotificationType) {
        Log.d(TAG,"handleCustomDataMessage")
        when(type){
            NotificationType.FLASH ,NotificationType.DISCOUNT->{

                val preferences = baseContext.getSharedPreferences(
                    PreferenceKeys.APP_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val lastNotification=preferences.getString(PreferenceKeys.LAST_FLASH_NOTIFICATION,null)
                val editor=preferences.edit()

                if (lastNotification==null){
                    showNotificationFlash(editor,title,message)
                }else{
                    if (title+message != lastNotification){
                        showNotificationFlash(editor,title,message)
                    }
                }
            }

            NotificationType.ORDER ->{
                showNotificationMessage(
                    this,
                    title,
                    message,
                    false,
                    true
                )
            }
        }


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