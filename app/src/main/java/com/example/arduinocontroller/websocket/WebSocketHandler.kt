package com.example.arduinocontroller.websocket

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class WebSocketHandler {
    public var connected: Boolean = false
        private set
    public var failedToConnect: Boolean = false
        private set

    private lateinit var webSocket: WebSocket

    public fun start() {
        Log.d("Websocket", "Connecting")

        val client = OkHttpClient()

        val request: Request = Request
            .Builder()
            //.url("ws://192.168.2.5:8765")
            .url("https://echo.websocket.org/") // test
            .build()

        val listener = ArduinoSocketListener(
            onReady = { websocketReady() },
            onClosed = { Log.d("Websocket", "Connection closed") },
            onMessage = { message -> Log.d("Websocket", "Received message: $message") },
            onFailure = { throwable -> websocketFailed(throwable) }
        )
        webSocket = client.newWebSocket(request, listener)
    }

    public fun waitConnection() {
        while (!connected || failedToConnect) {
            // Wait for the websocket to be ready
            Thread.sleep(100)
        }
    }

    public fun send(message: String) {
        webSocket.send(message)
        Log.d("Websocket", "Sent message: $message")
    }

    private fun websocketReady() {
        connected = true
    }

    private fun websocketFailed(throwable: Throwable) {
        failedToConnect = true
        Log.e("Websocket", "Failed to connect")
    }
}