package com.example.subscriberapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "messages.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE Messages (id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, speed INTEGER, timestamp INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Messages")
        onCreate(db)
    }

    fun insertMessage(message: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("message", message)
        }
        db.insert("Messages", null, values)
    }

    fun getAllMessages(): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT message FROM Messages", null)
        val messages = mutableListOf<String>()

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val message = cursor.getString(cursor.getColumnIndexOrThrow("message"))
                    messages.add(message)
                } while (cursor.moveToNext())
            }
        }

        return messages
    }

    fun getSpeedsInRange(startTime: Long, endTime: Long): List<Int> {
        val db = readableDatabase
        val speeds = mutableListOf<Int>()

        val cursor = db.rawQuery(
            "SELECT speed FROM Messages WHERE timestamp BETWEEN ? AND ?",
            arrayOf(startTime.toString(), endTime.toString())
        )

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val speed = cursor.getInt(cursor.getColumnIndexOrThrow("speed"))
                    speeds.add(speed)
                } while (cursor.moveToNext())
            }
        }

        return speeds
    }
}