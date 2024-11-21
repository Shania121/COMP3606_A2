package com.example.subscriberapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.util.*

class MainActivity : AppCompatActivity() {

    private var client: Mqtt5BlockingClient? = null
    private lateinit var tvReceivedMessage: TextView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvReceivedMessage = findViewById(R.id.tv_received_message)
        databaseHelper = DatabaseHelper(this)

        client = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.sundaebytestt.com")
            .serverPort(1883)
            .buildBlocking()

        try {
            client?.connect()
            client?.subscribeWith()?.topicFilter("assignment/location")?.send()
            Toast.makeText(this, "Subscribed to topic!", Toast.LENGTH_SHORT).show()

            client?.toAsync()?.publishes(MqttGlobalPublishFilter.ALL) { message ->
                val payload = String(message.payloadAsBytes ?: ByteArray(0), Charsets.UTF_8)
                runOnUiThread {
                    tvReceivedMessage.text = payload
                }

                databaseHelper.insertMessage(payload)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to subscribe: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun navigateToMapActivity(view: View) {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        client?.disconnect()
    }
}
