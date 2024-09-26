package com.example.timemanagementapp

import Task
import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.*

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var countDownTimer: CountDownTimer
    private var running: Boolean = false
    private var completionTimeInMillis: Long = 0
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var progressBar: ProgressBar
    private lateinit var saveButton: Button
    private lateinit var completedCheckbox: CheckBox
    private var timeRemainingInMillis: Long = 0 // Holds the remaining time after pause or resume
    private lateinit var timerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Retrieve the task object using Parcelable
        val task = intent.getParcelableExtra<Task>("task")
        if (task == null) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        val taskNameTextView = findViewById<TextView>(R.id.task_name_detail)
        val taskDescriptionTextView = findViewById<TextView>(R.id.task_description_detail)
        val taskPriorityTextView = findViewById<TextView>(R.id.task_priority_detail)
        val taskCategoryTextView = findViewById<TextView>(R.id.task_category_detail)
        progressBar = findViewById(R.id.progress_bar)
        saveButton = findViewById(R.id.save_button)
        completedCheckbox = findViewById(R.id.task_completed_checkbox)
        timerTextView = findViewById(R.id.timer_text_view) // TextView for displaying time left

        taskNameTextView.text = task.name
        taskDescriptionTextView.text = task.description
        taskPriorityTextView.text = task.priority
        taskCategoryTextView.text = task.category

        // Parse the completion time into milliseconds
        completionTimeInMillis = parseCompletionTime(task.completionTime)
        timeRemainingInMillis = completionTimeInMillis // Initially, the remaining time is the same as completion time

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the max progress of the progress bar based on completion time
        progressBar.max = 100 // Progress in percentage (0 to 100)

        // Start button for the countdown timer
        findViewById<Button>(R.id.start_button).setOnClickListener {
            if (!running) {
                startCountdown(timeRemainingInMillis)
                running = true
            }
        }

        // Stop button for the countdown timer
        findViewById<Button>(R.id.stop_button).setOnClickListener {
            if (running) {
                stopCountdown()
                running = false
            }
        }

        // Reset button for the countdown timer
        findViewById<Button>(R.id.reset_button).setOnClickListener {
            resetCountdown()
        }

        // Back button to finish the activity
        findViewById<Button>(R.id.back_button).setOnClickListener {
            finish()
        }

        // Checkbox listener to show the save button
        completedCheckbox.setOnCheckedChangeListener { _, isChecked ->
            saveButton.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Save button listener to move the task to completed
        saveButton.setOnClickListener {
            moveToCompletedTasks(task)
            finish() // Optionally finish this activity after saving
        }

        // Set Alarm button listener
        findViewById<Button>(R.id.set_alarm_button).setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun moveToCompletedTasks(task: Task) {
        Toast.makeText(this, "${task.name} has been marked as completed!", Toast.LENGTH_SHORT).show()
        // Add your logic to move the task to the completed tasks
    }

    private fun parseCompletionTime(time: String): Long {
        val parts = time.split(".")
        if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            return (minutes * 60 + seconds) * 1000L
        }
        return 0L
    }

    private fun startCountdown(timeInMillis: Long) {
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingInMillis = millisUntilFinished
                updateProgressAndTime(millisUntilFinished)
            }

            override fun onFinish() {
                running = false
                progressBar.progress = 100 // Ensure progress bar is fully filled
                timerTextView.text = "00:00"
                Toast.makeText(this@TaskDetailActivity, "Task time completed!", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun stopCountdown() {
        countDownTimer.cancel()
    }

    private fun resetCountdown() {
        stopCountdown()
        timeRemainingInMillis = completionTimeInMillis
        updateProgressAndTime(timeRemainingInMillis)
        running = false
        progressBar.progress = 0  // Reset progress bar on reset
    }

    private fun updateProgressAndTime(millisUntilFinished: Long) {
        // Calculate progress as the proportion of elapsed time
        val progress = (((completionTimeInMillis - millisUntilFinished).toFloat() / completionTimeInMillis) * 100).toInt()
        progressBar.progress = progress

        // Update timer text view with remaining time in mm:ss format
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                setAlarm(calendar.timeInMillis)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        )
        timePicker.show()
    }

    private fun setAlarm(timeInMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    this,
                    "Please allow this app to schedule exact alarms in settings.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
                return
            }
        }

        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            Toast.makeText(this, "Alarm set!", Toast.LENGTH_SHORT).show() // Show toast when alarm is set
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Failed to set alarm. Check your permissions.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    class AlarmReceiver : BroadcastReceiver() {
        private lateinit var ringtone: Ringtone

        override fun onReceive(context: Context, intent: Intent) {
            playAlarmSound(context)
            showAlarmNotification(context)

            // Stop the alarm after a specified duration (e.g., 5 seconds)
            Handler(Looper.getMainLooper()).postDelayed({
                stopAlarm()
            }, 5000) // Adjust this duration as needed
        }

        private fun playAlarmSound(context: Context) {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone.play()
        }

        private fun stopAlarm() {
            if (this::ringtone.isInitialized && ringtone.isPlaying) {
                ringtone.stop()
            }
        }

        private fun showAlarmNotification(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "alarm_channel",
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableVibration(true)
                    vibrationPattern = longArrayOf(100, 200, 100, 200)
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Alarm Ringing")
                .setContentText("Your alarm is ringing!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notificationManager.notify(1, notificationBuilder.build())
        }
    }
}
