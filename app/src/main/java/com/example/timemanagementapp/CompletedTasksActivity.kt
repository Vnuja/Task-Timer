package com.example.timemanagementapp

import Task
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CompletedTasksActivity : AppCompatActivity() {

    private lateinit var completedTasksTextView: TextView
    private lateinit var deleteAllButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_tasks)

        completedTasksTextView = findViewById(R.id.completed_tasks_text_view)
        deleteAllButton = findViewById(R.id.delete_all_button)
        backButton = findViewById(R.id.back_button)

        // Load completed tasks
        displayCompletedTasks()

        // Set up button listeners
        deleteAllButton.setOnClickListener {
            deleteAllTasks()
        }

        backButton.setOnClickListener {
            finish()  // Close the activity and return to the previous one
        }
    }

    private fun displayCompletedTasks() {
        val completedTasks = getCompletedTasks()

        // Format and display completed tasks
        completedTasksTextView.text = completedTasks.joinToString("\n\n") { task ->
            "${task.name}\nDescription: ${task.description}\nPriority: ${task.priority}\nCompletion Time: ${task.completionTime}\nCategory: ${task.category}\n" +
                    "----------------------------------"
        }
    }

    private fun getCompletedTasks(): List<Task> {
        // Replace this with actual data retrieval logic
        return listOf(
            Task("Task 1", "Description for Task 1", "High", "5.00", "Work"),
            Task("Task 2", "Description for Task 2", "Medium", "10.00", "Personal")
        )
    }

    private fun deleteAllTasks() {
        // Logic to clear the completed tasks from your data source
        // For demonstration, you can simply clear the TextView
        completedTasksTextView.text = "No completed tasks."
        // Optionally, show a message to confirm deletion
        Toast.makeText(this, "All completed tasks have been deleted.", Toast.LENGTH_SHORT).show()
    }
}
