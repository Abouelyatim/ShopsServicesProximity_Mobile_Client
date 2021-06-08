package com.smartcity.client.ui.main.account.orders.notification

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.smartcity.client.R
import com.smartcity.client.ui.auth.AuthActivity
import kotlin.random.Random


class NotificationUtils(val base: Context) : ContextWrapper(base) {
    var NOTIFICATION_ID = 100

    init {
        NOTIFICATION_ID= Random.nextInt()
    }

    fun returnNotification(
        title:String,
        body:String,
        shouldSound:Boolean,
        shouldVibrate:Boolean,
        headsUp: Boolean
    ): Notification {
        if (isOreoOrAbove()) {
            setupNotificationChannels()
        }
        return makeNotification(title,body,shouldSound,shouldVibrate,headsUp)
    }



    fun showNotificationMessage(
        title:String,
        body:String,
        shouldSound:Boolean,
        shouldVibrate:Boolean,
        headsUp: Boolean
    ){
        if (isOreoOrAbove()) {
            setupNotificationChannels()
        }
        getManager().notify(NOTIFICATION_ID,
            makeNotification(title,body,shouldSound,shouldVibrate,headsUp))
    }

    private fun setupNotificationChannels() {
        registerNormalNotificationChannel(getManager())
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun registerNormalNotificationChannel(notificationManager: NotificationManager) {
        val channel_all = NotificationChannel(
            "CHANNEL_ID_ALL",
            "CHANNEL_NAME_ALL",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel_all.enableLights(true)
        channel_all.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        channel_all.enableVibration(true)
        notificationManager.createNotificationChannel(channel_all)
        val channel_sound = NotificationChannel(
            "CHANNEL_ID_SOUND",
            "CHANNEL_NAME_ALL",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel_sound.enableVibration(false)
        notificationManager.createNotificationChannel(channel_sound)
        val channel_vibrate = NotificationChannel(
            "CHANNEL_ID_VIBRATE",
            "CHANNEL_NAME_ALL",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel_vibrate.setSound(null, null)
        channel_vibrate.enableVibration(true)
        notificationManager.createNotificationChannel(channel_vibrate)
        val channel_none = NotificationChannel(
            "CHANNEL_ID_NONE",
            "CHANNEL_NAME_ALL",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel_none.setSound(null, null)
        channel_none.enableVibration(false)
        notificationManager.createNotificationChannel(channel_none)
    }

    fun makeNotification(
        title:String,
        body:String,
        shouldSound:Boolean,
        shouldVibrate:Boolean,
        headsUp:Boolean
    ) : Notification {
        val builderHeadsUp: NotificationCompat.Builder =
            NotificationCompat.Builder(base, getChannelId(shouldSound,shouldVibrate))
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_stat_notify_more)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setContentText(body)


        val intent = Intent(base, AuthActivity::class.java)

        val pendingIntentHeadsUp = PendingIntent.getActivity(
            base,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        builderHeadsUp.setContentIntent(pendingIntentHeadsUp)

        if (headsUp){
            builderHeadsUp.setFullScreenIntent(pendingIntentHeadsUp,true)
        }


        if (shouldSound && !shouldVibrate) {
            playSound()
            builderHeadsUp.setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(longArrayOf(0L))
        }
        if (shouldVibrate && !shouldSound) {
            builderHeadsUp.setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(null)
        }
        if (shouldSound && shouldVibrate) {
            playSound()
            builderHeadsUp.setDefaults(Notification.DEFAULT_ALL)
        }

        return builderHeadsUp.build()

    }

    private fun getChannelId(
        shouldSound:Boolean,
        shouldVibrate:Boolean
    ): String {
        return if (shouldSound && shouldVibrate) {
            "CHANNEL_ID_ALL"
        } else if (shouldSound && !shouldVibrate) {
            "CHANNEL_ID_SOUND"
        } else if (!shouldSound && shouldVibrate) {
            "CHANNEL_ID_VIBRATE"
        } else {
            "CHANNEL_ID_NONE"
        }
    }

    private fun playSound(){
        startService(Intent(this, NotificationSoundService::class.java))
    }

    internal class NotificationSoundService : Service() {
        var mediaPlayer: MediaPlayer? = null

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

        override fun onCreate() {
            super.onCreate()
            mediaPlayer = MediaPlayer.create(this, R.raw.notification)
            mediaPlayer!!.isLooping = false
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            super.onStartCommand(intent, flags, startId)
            mediaPlayer!!.start()
            return START_NOT_STICKY
        }

        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer!!.stop()
        }
    }

    private fun isOreoOrAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun  getManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}