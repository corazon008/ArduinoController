package com.example.arduinocontroller.ble

import java.util.*
import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import java.util.UUID

class BLEManager {
    val MOVEMENT_SERVICE_UUID: UUID =
        UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb") // BLE Movement service
    val CHARACTERISTIC_UUID: UUID =
        UUID.fromString("00002A57-0000-1000-8000-00805f9b34fb") // Switch

    val SPEED_SERVICE_UUID: UUID =
        UUID.fromString("0000180B-0000-1000-8000-00805f9b34fb") // Speed service
    val SPEED_CHARACTERISTIC_UUID: UUID =
        UUID.fromString("00002A58-0000-1000-8000-00805f9b34fb") // Speed characteristic

    private val TAG = "BLEManager"
    private lateinit var context: Context
    private var bluetoothGatt: BluetoothGatt? = null
    private var handler: BLEHandler? = null

    private var scanRunning = false

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private val bleScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    fun init(appContext: Context) {
        context = appContext.applicationContext
        scanRunning = false
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    suspend fun startScan(callback: (BluetoothDevice) -> Unit): Boolean {
        if (scanRunning) {
            Log.e(TAG, "Scan already running")
            return false
        }
        scanRunning = true
        val filter = ScanFilter.Builder()
 //           .setServiceUuid(ParcelUuid(SERVICE_UUID))
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
            scanRunning = false
            Toast.makeText(context, "Scan completed", Toast.LENGTH_SHORT).show()
        }, 2000)
        return true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        handler = BLEHandler()
        bluetoothGatt = device.connectGatt(context, false, handler!!)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun writeMovement(value: Byte) {
        val gatt = bluetoothGatt ?: return
        val service = gatt.getService(MOVEMENT_SERVICE_UUID) ?: return
        val characteristic = service.getCharacteristic(CHARACTERISTIC_UUID) ?: return

        characteristic.value = byteArrayOf(value)
        gatt.writeCharacteristic(characteristic)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun writeSpeed(value: Byte) {
        val gatt = bluetoothGatt ?: return
        val service = gatt.getService(SPEED_SERVICE_UUID) ?: return
        val characteristic = service.getCharacteristic(SPEED_CHARACTERISTIC_UUID) ?: return

        Log.d(TAG, "Writing speed value: $value to characteristic: $SPEED_CHARACTERISTIC_UUID")

        characteristic.value = byteArrayOf(value)
        gatt.writeCharacteristic(characteristic, byteArrayOf(value), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        handler = null
    }
}