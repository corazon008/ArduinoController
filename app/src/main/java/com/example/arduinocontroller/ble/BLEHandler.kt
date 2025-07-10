package com.example.arduinocontroller.ble


import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*

import android.util.Log
import androidx.annotation.RequiresPermission

class BLEHandler : BluetoothGattCallback() {

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            Log.d("BLEHandler", "Connected to GATT server.")
            gatt.discoverServices()
        } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            Log.d("BLEHandler", "Disconnected from GATT server.")
        }
    }

/*    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        val service: BluetoothGattService? = gatt.getService(BLEManager.SERVICE_UUID)
        val characteristic: BluetoothGattCharacteristic? =
            service?.getCharacteristic(BLEManager.CHARACTERISTIC_UUID)

        if (characteristic != null) {
            Log.d("BLEHandler", "LED characteristic found. Writing value...")
            characteristic.setValue(byteArrayOf(1)) // 🔴 rouge
            gatt.writeCharacteristic(characteristic)
        } else {
            Log.e("BLEHandler", "LED service/characteristic not found.")
        }
    }*/

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        Log.d("BLEHandler", "Characteristic write status: $status")
    }
}