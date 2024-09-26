import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.timemanagementapp.R
import java.util.Calendar

class ReminderActivity : AppCompatActivity() {

    private lateinit var reminderTimeInput: EditText
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        createNotificationChannel() // Create notification channel

        reminderTimeInput = findViewById(R.id.reminder_time_input)

        findViewById<Button>(R.id.set_reminder_button).setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            setReminder(selectedHour, selectedMinute)
        }, hour, minute, true).show()
    }

    private fun setReminder(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Check if the time is in the future
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            Toast.makeText(this, "Please set a future time!", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate the delay until the reminder
        val delay = calendar.timeInMillis - System.currentTimeMillis()

        // Schedule the notification
        handler.postDelayed({
            showNotification("Reminder", "It's time for your reminder!")
        }, delay)
    }

    private fun showNotification(title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(this, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.drawable.alrm) // Use your notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "YOUR_CHANNEL_ID", // Use a unique channel ID
                "Reminder Notifications", // Channel name
                NotificationManager.IMPORTANCE_HIGH // Importance level
            ).apply {
                description = "Channel for reminder notifications"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
