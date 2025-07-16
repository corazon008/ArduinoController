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
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.arduinocontroller.ble.BLEManager
import com.example.arduinocontroller.databinding.ActivityMainBinding

import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bleManager: BLEManager
    private lateinit var viewModel: MainActivityView

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainActivityView::class.java]

        val devicesListLayout: LinearLayout = binding.devicesList

        binding.refreshButton.setOnClickListener {
            viewModel.refreshBleDevices(this)
        }

        viewModel.bleDevices.observe(this) {
            Log.d("MainActivityObserver", "Devices list updated: ${it.size} devices found")
            devicesListLayout.removeAllViews()
            for (device in it) {
                addDeviceToView(devicesListLayout, device)
            }
        }

        viewModel.refreshBleDevices(this)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun addDeviceToView(devicesListLayout: LinearLayout, device: BluetoothDevice) {
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
