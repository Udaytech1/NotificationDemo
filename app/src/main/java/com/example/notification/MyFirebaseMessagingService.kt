package com.example.notification

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
    //        notificationSound =
    //            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.getPackageName() + "/" + R.raw.notification)
        // Here is FILE_NAME is the name of file that you want to play

    //        System.out.println("notification==================" + remoteMessage.notification);
    //        System.out.println("data==================" + remoteMessage.data)
        parseNotification(remoteMessage.data)

    }

    override fun onNewToken(token_str: String) {
        super.onNewToken(token_str)
        System.out.println("token_str==================" + token_str)
    }


    fun parseNotification(data: Map<String, String>) {
        playNotifSound()
        val notificationObject = JSONObject(data)
        System.out.println("dataObject=====" + notificationObject)
        val notificationIntent: Intent
            notificationIntent = Intent(applicationContext, MainActivity::class.java)

        notificationIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingNotificationIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, PendingIntent.FLAG_ONE_SHOT
        ) as PendingIntent
        try {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: Notification.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                notificationBuilder = Notification.Builder(this, CHANNEL_ID)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingNotificationIntent)
            } else {
                notificationBuilder = Notification.Builder(this)
                    .setAutoCancel(true)
                    .setLights(Color.RED, 1, 1)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingNotificationIntent)
            }

            notificationBuilder.setContentText(notificationObject.getString("body").trim())
            notificationBuilder.setContentTitle(notificationObject.getString("title").trim())
            notificationBuilder.setAutoCancel(true)
//            notificationBuilder.style =
//                Notification.BigTextStyle().bigText(notificationObject.getString("body").trim())
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
//            notificationBuilder.style = Notification.BigPictureStyle()
//                .setBigContentTitle("Femme")

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(
                System.currentTimeMillis().toInt(),
                notificationBuilder.build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        //        private var notificationSound: Uri? = null
        private val CHANNEL_ID = "Femme"
        private val CHANNEL_NAME = "Femme"
        val notificationChannel: NotificationChannel
            @RequiresApi(api = Build.VERSION_CODES.O)
            get() {
                /*     val attributes = AudioAttributes.Builder()
                         .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                         .build()*/
                val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.enableLights(true)
                notificationChannel.enableVibration(true)
//                notificationChannel.setSound(notificationSound, attributes);
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationChannel.lightColor = Color.RED
                notificationChannel.vibrationPattern = longArrayOf(500, 500, 500, 500, 500)
                notificationChannel.setShowBadge(true)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                return notificationChannel
            }
    }

    private fun playNotifSound() {
        if (!isAppInForeground(this)) {
            val mMediaPlayer: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.toon)
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.isLooping = false
            mMediaPlayer.start()
        }
    }

    private fun isAppInForeground(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val foregroundTaskInfo = am.getRunningTasks(1)[0]
            val foregroundTaskPackageName = foregroundTaskInfo.topActivity!!.packageName
            foregroundTaskPackageName.lowercase(Locale.getDefault()) == context.packageName.lowercase(
                Locale.getDefault()
            )
        } else {
            val appProcessInfo = RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(appProcessInfo)
            if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
                return true
            }
            val km = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            // App is foreground, but screen is locked, so show notification
            km.inKeyguardRestrictedInputMode()
        }
    }

}
