package com.example.timemanagementapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.Button
import android.widget.ImageButton

class ProfileActivity : AppCompatActivity() {

    private lateinit var workTasksButton: Button
    private lateinit var personalTasksButton: Button
    private lateinit var completedTasksButton: Button // New button for completed tasks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Home button functionality
        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Initialize buttons
        workTasksButton = findViewById(R.id.view_work_tasks_button)
        personalTasksButton = findViewById(R.id.view_personal_tasks_button)
        completedTasksButton = findViewById(R.id.view_completed_tasks_button) // Initialize new button

        // Set button click listeners
        workTasksButton.setOnClickListener {
            val intent = Intent(this, WorkTasksActivity::class.java)
            startActivity(intent)
        }

        personalTasksButton.setOnClickListener {
            val intent = Intent(this, PersonalTasksActivity::class.java)
            startActivity(intent)
        }

        completedTasksButton.setOnClickListener { // Set listener for completed tasks button
            val intent = Intent(this, CompletedTasksActivity::class.java)
            startActivity(intent)
        }

        // Load tasks from storage (if you still need it for personal tasks)
        loadTasks()
    }

    private fun loadTasks() {
        // Load personal tasks from storage (if needed)
        // This part can be omitted if personal tasks are handled elsewhere
    }
}
