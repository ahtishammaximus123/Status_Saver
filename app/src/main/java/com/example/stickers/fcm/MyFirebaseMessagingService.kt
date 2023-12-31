package com.example.stickers.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.stickers.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import java.util.concurrent.atomic.AtomicInteger

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, "onMessageReceived: ")


        //Data available
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            var icon = ""
            var title = ""
            var shortDesc = ""
            var longDesc = ""
            var image = ""
            var packageName =""
            //fetch data into variables
            try {
                icon = remoteMessage.data["icon"].toString()
                title = remoteMessage.data["title"].toString()
                shortDesc = remoteMessage.data["short_desc"].toString()
                longDesc = remoteMessage.data["long_desc"].toString()
                image = remoteMessage.data["feature"].toString()
                packageName = remoteMessage.data["app_url"]?.split("=")?.get(1).toString()
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }


            //Check already installed app
            if (packageName != null) {
                val alreadyInstalled = isAppInstalled(packageName, this)
                if (alreadyInstalled) return

                //send notification
                if (icon == "" || title == "" || shortDesc == "") {
                    return
                } else {
                    Handler(this.mainLooper).post {
                        sendNotification(icon, title, shortDesc, image, longDesc, packageName)
                    }
                }
            }
        }
    }

    private fun sendNotification(
        icon: String,
        title: String,
        shortDesc: String,
        image: String?,
        longDesc: String?,
        storePackage: String
    ) {

        //Open PlayStore
        val intent = try {
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$storePackage"))
        } catch (e: ActivityNotFoundException) {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.googlee.com/store/apps/details?id=$storePackage")
            )
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        Log.i(TAG, "longDesc: $longDesc")

        //Make Remote Views For text
        val remoteViews = RemoteViews(packageName, R.layout.firebase_notification_view)
        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_short_desc, shortDesc)

        //Notification Parameters
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        //Build Notification
        val notificationID = getNextInt()
        notificationManager.notify(notificationID, notificationBuilder.build())

        //Set Images into remoteViews
        try {
            if (icon != "") {
                Picasso.with(this).load(icon)
                    .into(remoteViews, R.id.iv_icon, notificationID, notificationBuilder.build())
            }
            if (image != "") {
                remoteViews.setViewVisibility(R.id.iv_feature, View.VISIBLE)
                Picasso.with(this).load(image)
                    .into(remoteViews, R.id.iv_feature, notificationID, notificationBuilder.build())
            }
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

    }

    private fun isAppInstalled(uri: String, context: Context): Boolean {
        val pm = context.packageManager
        return try {
            val applicationInfo = pm.getApplicationInfo(uri, 0)
            applicationInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private val seed = AtomicInteger()
        fun getNextInt(): Int {
            return seed.incrementAndGet()
        }
    }

    override fun onNewToken(p0: String) {

    }
}