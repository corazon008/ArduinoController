package com.example.arduinocontroller

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.example.arduinocontroller.ble.BLEManager
import com.example.arduinocontroller.databinding.ActivityMainBinding

import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bleManager: BLEManager

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bleManager = BLEManager()
        var devicesList: MutableList<BluetoothDevice> = mutableListOf()
        val devicesListLayout: LinearLayout = binding.devicesList
        devicesListLayout.removeAllViews()

        bleManager.init(applicationContext)

        bleManager.startScan { device ->
            Log.d("MainActivity", "Device found: ${device.name} - ${device.address}")
            Log.d("MainActivity", devicesList.toString())
            if (!devicesList.contains(device)) {
                devicesList.add(device)
                // Add the device to the UI
                addDeviceToView(devicesListLayout, device)
                Log.d("MainActivity", "Device found: ${device.name} - ${device.address}")
            } else {
                Log.d("MainActivity", "Device already in list: ${device.name} - ${device.address}")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun addDeviceToView(devicesListLayout: LinearLayout, device : BluetoothDevice) {
        val rowLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER
        }
        val styledContext = ContextThemeWrapper(this, R.style.CenteredTextView)
        val textView = TextView(styledContext).apply {
            text = device.name ?: "Unknown Device (${device.address})"
        }

        val connectButton = Button(this)
        connectButton.text = "Connect"
        connectButton.setOnClickListener {
            Log.d("MainActivity", "Connecting to device: ${device.name} - ${device.address}")
            // Go to ControllerActivity
            val intent = Intent(this, ControllerActivity::class.java).apply {
                putExtra("DEVICE", device)
            }
            startActivity(intent)
        }
        rowLayout.addView(textView)
        rowLayout.addView(connectButton)

        devicesListLayout.addView(rowLayout)
    }
}
