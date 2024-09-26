package com.example.timemanagementapp

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat


class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Get the reminder time from the intent
        val taskTime = intent.getStringExtra("TASK_TIME") ?: "N/A" // Default to "N/A" if not found

        // Check permissions before triggering notifications
        if (hasNotificationPermission(context)) {
            createNotificationChannel(context)
            showNotification(context, taskTime) // Show the notification with task time
        } else {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Check if the app has notification permission (relevant for Android 13+)
    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Notifications don't require explicit permission on older Android versions
            true
        }
    }

    // Create a notification channel, required for Android 8.0+
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Notifications"
            val descriptionText = "Channel for reminder alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    // Show a notification with task time
    private fun showNotification(context: Context, taskTime: String) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build the notification
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.alrm)
                .setContentTitle("Reminder Alert")
                .setContentText("Its time to start your task!")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Your reminder time has been reached! Task Time: $taskTime")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            // Show the notification
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID, notificationBuilder.build())
        } catch (e: SecurityException) {
            Toast.makeText(
                context,
                "Notification failed due to lack of permission.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
