package com.example.timemanagementapp

import Task
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WorkTasksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var workTasks: ArrayList<Task>
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_tasks)

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_work_tasks)
        backButton = findViewById(R.id.back_button)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load work tasks
        workTasks = TaskStorage.loadWorkTasks(this)

        // Set adapter for RecyclerView
        taskAdapter = TaskAdapter(this, workTasks)
        recyclerView.adapter = taskAdapter

        // Set up back button functionality
        backButton.setOnClickListener {
            finish() // Close the current activity and return to the previous one
        }

        // Optionally, you can add a message if no tasks are available
        if (workTasks.isEmpty()) {
            // You can show a Toast or some other message indicating no tasks
        }
    }
}
