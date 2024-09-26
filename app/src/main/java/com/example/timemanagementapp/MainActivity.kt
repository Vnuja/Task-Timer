package com.example.timemanagementapp

import Task
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val tasks = ArrayList<Task>()
    private var reminderTimeInMillis: Long = 0L // To store reminder time
    private lateinit var themeSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        setTheme(if (sharedPreferences.getBoolean("dark_theme", false)) R.style.Theme_TimeManagementApp else R.style.Theme_TimeManagementApp_Light)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(this, tasks)
        recyclerView.adapter = taskAdapter

        // Initialize Theme Switch
        themeSwitch = findViewById(R.id.theme_switch)
        themeSwitch.isChecked = sharedPreferences.getBoolean("dark_theme", false)
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_theme", isChecked)
            editor.apply()
            recreate() // Recreate the activity to apply the new theme
        }

        // Add Task Button
        findViewById<Button>(R.id.add_task_button).setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, 1) // Request code 1 for adding new task
        }

        // Stopwatch Button
        findViewById<Button>(R.id.timer_button).setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        // Set Reminder Button
        findViewById<Button>(R.id.set_reminder_button).setOnClickListener {
            showTimePicker()
        }

        // Profile Icon Click
        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            val workTasks = TaskStorage.loadWorkTasks(this)
            val personalTasks = TaskStorage.loadPersonalTasks(this)

            val intent = Intent(this, ProfileActivity::class.java).apply {
                putParcelableArrayListExtra("WORK_TASKS", workTasks)
                putParcelableArrayListExtra("PERSONAL_TASKS", personalTasks)
            }
            startActivity(intent)
        }

        // Load previously saved tasks
        loadTasks()
    }

    // TimePickerDialog to set reminder
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            setReminder(selectedHour, selectedMinute)
        }, hour, minute, true).show()
    }

    // Function to set the reminder and trigger the notification
    private fun setReminder(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        reminderTimeInMillis = calendar.timeInMillis

        // Schedule the notification
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("TASK_TIME", "$hour:$minute")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Reminder set for $hour:$minute", Toast.LENGTH_SHORT).show()
    }

    // Handle result when returning from AddTaskActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val task = data.getParcelableExtra<Task>("task")
            val isEditing = data.getBooleanExtra("isEditing", false)
            if (task != null) {
                if (isEditing) {
                    // Update the entire task
                    val position = tasks.indexOfFirst { it.name == task.name }
                    if (position != -1) {
                        tasks[position] = task // Replace the old task with the edited one
                        taskAdapter.notifyItemChanged(position)
                    }
                } else {
                    // Add new task
                    tasks.add(task)
                    taskAdapter.notifyItemInserted(tasks.size - 1)
                }
                // Log the task update for debugging
                Log.d("MainActivity", "Task Updated: Name=${task.name}, Description=${task.description}, Priority=${task.priority}, Category=${task.category}")
                // Save tasks to storage
                TaskStorage.saveTasks(this, getWorkTasks(), getPersonalTasks())
            }
        }
    }

    // Load tasks from storage
    private fun loadTasks() {
        tasks.clear()
        tasks.addAll(TaskStorage.loadWorkTasks(this))
        tasks.addAll(TaskStorage.loadPersonalTasks(this))
        taskAdapter.notifyDataSetChanged()
    }

    // Helper methods to retrieve work and personal tasks
    private fun getWorkTasks(): ArrayList<Task> {
        return tasks.filter { it.category == "Work" } as ArrayList<Task>
    }

    private fun getPersonalTasks(): ArrayList<Task> {
        return tasks.filter { it.category == "Personal" } as ArrayList<Task>
    }
}
