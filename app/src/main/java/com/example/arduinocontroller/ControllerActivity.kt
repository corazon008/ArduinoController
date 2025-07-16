package com.example.arduinocontroller

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.example.arduinocontroller.ble.BLEManager
import com.example.arduinocontroller.databinding.ActivityControllerBinding


class ControllerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControllerBinding
    private lateinit var bleManager: BLEManager
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var forwardButton: ImageButton
    private lateinit var backwardButton: ImageButton
    private lateinit var stopButton: ImageButton

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

        leftButton = binding.left
        rightButton = binding.right
        forwardButton = binding.forward
        backwardButton = binding.backward
        stopButton = binding.stop

        forwardButton.setOnClickListener {
            sendCommand(1)
        }
        backwardButton.setOnClickListener {
            sendCommand(2)
        }
        leftButton.setOnClickListener {
            sendCommand(3)
        }
        rightButton.setOnClickListener {
            sendCommand(4)
        }

        stopButton.setOnClickListener {
             sendCommand(5)
        }

        binding.speedSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // TODO Auto-generated method stub
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
val speed = progress * 255 / 100
                binding.speedTextView.text = speed.toString()
                bleManager.writeSpeed(speed.toByte())
            }
        });

        // Initialize video streaming if needed
        //initializeVideoStreaming()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun sendCommand(b: Byte) {
        Log.d("ControllerActivity", "Sending command: ${b}")
        bleManager.writeMovement(b)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources if needed
    }
}