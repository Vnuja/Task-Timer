package com.example.timemanagementapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder

class AlarmService : Service() {
    private var ringtone: Ringtone? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playAlarmSound()
        return START_STICKY
    }

    private fun playAlarmSound() {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
        ringtone?.play()

        // Create a foreground notification to keep the service alive
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        return Notification.Builder(this, "alarm_channel")
            .setContentTitle("Alarm")
            .setContentText("Alarm is ringing!")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(Notification.PRIORITY_HIGH) // Use this line for backward compatibility
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone?.stop()
        ringtone = null // Clean up
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
