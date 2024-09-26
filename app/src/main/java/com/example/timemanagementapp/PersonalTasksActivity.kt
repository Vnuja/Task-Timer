package com.example.timemanagementapp

import Task
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class PersonalTasksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var personalTasks: ArrayList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_tasks)

        recyclerView = findViewById(R.id.recycler_view_personal_tasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load personal tasks from storage
        personalTasks = TaskStorage.loadPersonalTasks(this)

        // Set up the adapter and RecyclerView
        taskAdapter = TaskAdapter(this, personalTasks)
        recyclerView.adapter = taskAdapter

        // Handle back button press
        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
