package com.example.publisherapplication

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.util.*

class MainActivity : AppCompatActivity() {
    private var client: Mqtt5BlockingClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        client = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.sundaebytestt.com")
            .serverPort(1883)
            .buildBlocking()
    }

    private fun publishLocation(location: Location) {
        val message = "Location Update: ${location.latitude}, ${location.longitude}"
        try {
            client?.publishWith()?.topic("assignment/location")?.payload(message.toByteArray())
                ?.send()
            Toast.makeText(this, "Location Published", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to publish location", Toast.LENGTH_SHORT).show()
        }
    }

    fun startPublishing(view: View) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        try {
            client?.connect() // Connect to the MQTT broker
            Toast.makeText(this, "Publishing Started!", Toast.LENGTH_SHORT).show()

            // Simulate location updates for simplicity
            val dummyLocation = Location("")
            dummyLocation.latitude = 10.12345
            dummyLocation.longitude = -61.98765
            publishLocation(dummyLocation)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to connect to broker", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopPublishing(view: View) {
        try {
            client?.disconnect() // Disconnect from the MQTT broker
            Toast.makeText(this, "Publishing Stopped!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to disconnect from broker", Toast.LENGTH_SHORT).show()
        }
    }
}
