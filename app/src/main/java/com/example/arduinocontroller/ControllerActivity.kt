package com.example.arduinocontroller

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.VideoView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.example.arduinocontroller.ble.BLEManager
import com.example.arduinocontroller.databinding.ActivityControllerBinding
import com.example.arduinocontroller.websocket.WebSocketHandler

class ControllerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControllerBinding
    private lateinit var bleManager: BLEManager
    private lateinit var videoView: VideoView
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var forwardButton: Button
    private lateinit var backwardButton: Button
    private lateinit var stopButton: Button

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControllerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // retrieve BLE device address from intent
        val device: BluetoothDevice? = intent.getParcelableExtra("DEVICE", BluetoothDevice::class.java)
        if (device == null) {
            Log.e("ControllerActivity", "No device address provided")
            finish() // Close the activity if no device address is provided
            return
        }

        bleManager = BLEManager()
        bleManager.init(applicationContext)
        bleManager.connectToDevice(device)

        videoView = binding.videoView
        leftButton = binding.left
        rightButton = binding.right
        forwardButton = binding.forward
        backwardButton = binding.backward
        //stopButton = binding.stop

        leftButton.setOnClickListener {
            sendCommand(1)
        }
        rightButton.setOnClickListener {
            sendCommand(2)
        }
        forwardButton.setOnClickListener {
            sendCommand(3)
        }
        backwardButton.setOnClickListener {
            sendCommand(4)
        }

        // Uncomment if you want to implement the stop button
        // stopButton.setOnClickListener {
        //     sendCommand("stop")
        // }

        // Initialize video streaming if needed
        //initializeVideoStreaming()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun sendCommand(b: Byte) {
        Log.d("ControllerActivity", "Sending command: ${b}")
        bleManager.writeLedColor(b)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources if needed
    }
}