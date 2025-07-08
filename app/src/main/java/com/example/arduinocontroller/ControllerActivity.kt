package com.example.arduinocontroller

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.arduinocontroller.databinding.ActivityControllerBinding
import com.example.arduinocontroller.websocket.WebSocketHandler

class ControllerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControllerBinding
    private lateinit var webSocketHandler: WebSocketHandler
    private lateinit var videoView: VideoView
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var forwardButton: Button
    private lateinit var backwardButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControllerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webSocketHandler = WebSocketHandler()
        webSocketHandler.start()
        webSocketHandler.waitConnection()

        if (webSocketHandler.failedToConnect) {
            Log.e("ControllerActivity", "WebSocket connection failed, cannot proceed.")
            // kill the activity or show an error message
            finish() // Close the activity if WebSocket connection failed
            return
        }

        videoView = binding.videoView
        leftButton = binding.left
        rightButton = binding.right
        forwardButton = binding.forward
        backwardButton = binding.backward
        //stopButton = binding.stop

        leftButton.setOnClickListener {
            sendCommand(Movement.LEFT)
        }
        rightButton.setOnClickListener {
            sendCommand(Movement.RIGHT)
        }
        forwardButton.setOnClickListener {
            sendCommand(Movement.FORWARD)
        }
        backwardButton.setOnClickListener {
            sendCommand(Movement.BACKWARD)
        }

        // Uncomment if you want to implement the stop button
        // stopButton.setOnClickListener {
        //     sendCommand("stop")
        // }

        // Initialize video streaming if needed
        //initializeVideoStreaming()
    }

    private fun sendCommand(movement: Movement) {
        Log.d("ControllerActivity", "Sending command: ${Movement.toString(movement)}")
        webSocketHandler.send(Movement.toString(movement))
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources if needed
    }
}