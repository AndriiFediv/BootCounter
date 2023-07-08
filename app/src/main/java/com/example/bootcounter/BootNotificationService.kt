package com.example.bootcounter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bootcounter.database.BootEventDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BootNotificationService : Service() {

    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread

    override fun onCreate() {
        super.onCreate()
        handlerThread = HandlerThread("BootNotificationServiceHandler")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.IO).launch {
                    val specialBodyText = generateSpecialBodyText()
                    showNotification(specialBodyText)
                }
                handler.postDelayed(this, TimeUnit.MINUTES.toMillis(15))
            }
        }
        handler.post(runnable)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quitSafely()
    }

    private fun showNotification(notificationText: String) {
        val channelId = "boot_notification_channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Boot Notifications"
            val descriptionText = "Notifications for boot events"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Boot Event")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@BootNotificationService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, builder.build())
        }
    }

    private suspend fun generateSpecialBodyText(): String {
        val bootEvents = BootEventDatabase.getDatabase(this).bootEventDao().getAllBootEvents().first()

        return when (bootEvents.size) {
            0 -> "No boots detected"
            1 -> "The boot was detected with the timestamp = ${bootEvents[0].timestamp}"
            else -> {
                val lastBootEvent = bootEvents[bootEvents.size - 1]
                val preLastBootEvent = bootEvents[bootEvents.size - 2]
                val timeDelta = lastBootEvent.timestamp - preLastBootEvent.timestamp
                "Last boots time delta = $timeDelta"
            }
        }
    }
}