package com.example.arduinocontroller

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.arduinocontroller.ble.BLEManager
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivityView: ViewModel() {
    private val _bleDevices = MutableLiveData<MutableList<BluetoothDevice>>()
    val bleDevices: LiveData<MutableList<BluetoothDevice>> get() = _bleDevices
    private lateinit var bleManager: BLEManager


    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    fun refreshBleDevices(applicationContext: android.content.Context) {
        _bleDevices.value = mutableListOf() // au lieu de .clear()


        viewModelScope.launch {
            bleManager = BLEManager()
            bleManager.init(applicationContext)
            if(bleManager.startScan(startScanCallback)){
                Toast.makeText(applicationContext, "Scanning for devices...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    val startScanCallback: (BluetoothDevice) -> Unit = { device ->
        val currentList = _bleDevices.value ?: mutableListOf()

        // On ajoute uniquement si l'appareil n'existe pas déjà
        if (currentList.none { it.address == device.address }) {
            val updatedList = currentList.toMutableList().apply { add(device) }
            _bleDevices.postValue(updatedList)

            Log.d("MainActivityView", "Device added: ${device.name} - ${device.address}")
        } else {
            Log.d("MainActivityView", "Device already exists: ${device.name} - ${device.address}")
        }
    }

}