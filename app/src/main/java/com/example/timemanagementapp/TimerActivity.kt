package com.example.timemanagementapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TimerActivity : AppCompatActivity() {

    private lateinit var chronometer: Chronometer
    private var pauseOffset: Long = 0
    private var running: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        chronometer = findViewById(R.id.stopwatch)

        findViewById<Button>(R.id.start_button).setOnClickListener {
            if (!running) {
                chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                chronometer.start()
                running = true
            }
        }

        findViewById<Button>(R.id.stop_button).setOnClickListener {
            if (running) {
                chronometer.stop()
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
                running = false;
            }
        }

        findViewById<Button>(R.id.reset_button).setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime() // Reset to current time
            pauseOffset = 0
            if (!running) {
                chronometer.stop() // Ensure it stops when reset
            }
        }

        findViewById<Button>(R.id.back_button).setOnClickListener {
            finish()
        }
    }
}
