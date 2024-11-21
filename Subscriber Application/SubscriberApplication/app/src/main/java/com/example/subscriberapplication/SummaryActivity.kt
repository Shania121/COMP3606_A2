package com.example.subscriberapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SummaryActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var btnGenerateSummary: Button
    private lateinit var tvSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        etStartTime = findViewById(R.id.et_start_time)
        etEndTime = findViewById(R.id.et_end_time)
        btnGenerateSummary = findViewById(R.id.btn_generate_summary)
        tvSummary = findViewById(R.id.tv_summary)

        databaseHelper = DatabaseHelper(this)

        btnGenerateSummary.setOnClickListener {
            generateSummary()
        }
    }

    private fun generateSummary() {
        val startTime = etStartTime.text.toString()
        val endTime = etEndTime.text.toString()

        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please enter a valid time range.", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startDate: Date
        val endDate: Date
        try {
            startDate = dateFormat.parse(startTime)!!
            endDate = dateFormat.parse(endTime)!!
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid date format.", Toast.LENGTH_SHORT).show()
            return
        }

        val speeds = databaseHelper.getSpeedsInRange(startDate.time, endDate.time)
        if (speeds.isEmpty()) {
            tvSummary.text = "No data available for the selected range."
            return
        }

        val minSpeed = speeds.minOrNull() ?: 0
        val maxSpeed = speeds.maxOrNull() ?: 0
        val avgSpeed = speeds.average()

        tvSummary.text = """
            Minimum Speed: $minSpeed
            Maximum Speed: $maxSpeed
            Average Speed: ${"%.2f".format(avgSpeed)}
        """.trimIndent()
    }
}