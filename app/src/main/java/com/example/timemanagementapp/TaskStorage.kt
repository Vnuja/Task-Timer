package com.example.timemanagementapp

import Task
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TaskStorage {
    private const val WORK_TASKS_KEY = "work_tasks"
    private const val PERSONAL_TASKS_KEY = "personal_tasks"

    // Save tasks to SharedPreferences
    fun saveTasks(context: Context, workTasks: ArrayList<Task>, personalTasks: ArrayList<Task>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        // Convert task lists to JSON strings
        val workTasksJson = gson.toJson(workTasks)
        val personalTasksJson = gson.toJson(personalTasks)

        // Save JSON strings in SharedPreferences
        editor.putString(WORK_TASKS_KEY, workTasksJson)
        editor.putString(PERSONAL_TASKS_KEY, personalTasksJson)
        editor.apply()
    }

    // Load work tasks from SharedPreferences
    fun loadWorkTasks(context: Context): ArrayList<Task> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(WORK_TASKS_KEY, null)
        val type = object : TypeToken<ArrayList<Task>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    // Load personal tasks from SharedPreferences
    fun loadPersonalTasks(context: Context): ArrayList<Task> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(PERSONAL_TASKS_KEY, null)
        val type = object : TypeToken<ArrayList<Task>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }
}
