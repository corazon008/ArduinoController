package com.example.arduinocontroller.ble

import java.util.*
import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.UUID

class BLEManager {
    companion object{
        val SERVICE_UUID: UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb") // BLE LED service
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A57-0000-1000-8000-00805f9b34fb") // Switch
    }

    private val TAG = "BLEManager"
    private lateinit var context: Context
    private var bluetoothGatt: BluetoothGatt? = null
    private var handler: BLEHandler? = null

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private val bleScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(callback: (BluetoothDevice) -> Unit) {
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                callback(result.device)
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "Scan failed: $errorCode")
            }
        }

        bleScanner.startScan(listOf(filter), settings, scanCallback)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            bleScanner.stopScan(scanCallback)
            Log.d(TAG, "Scan stopped after timeout.")
        }, 2000)

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        handler = BLEHandler()
        bluetoothGatt = device.connectGatt(context, false, handler!!)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun writeLedColor(value: Byte) {
        val gatt = bluetoothGatt ?: return
        val service = gatt.getService(SERVICE_UUID) ?: return
        val characteristic = service.getCharacteristic(CHARACTERISTIC_UUID) ?: return

        characteristic.value = byteArrayOf(value)
        gatt.writeCharacteristic(characteristic)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        handler = null
    }
}