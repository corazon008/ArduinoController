package com.example.arduinocontroller.websocket

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class ArduinoSocketListener(
    private val onReady: () -> Unit,
    private val onClosed: () -> Unit,
    private val onMessage: (String) -> Unit = {},
    private val onFailure: (Throwable) -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        output("WebSocket opened: ${response.message}")
        onReady()
        // You can send an initial message if needed
        webSocket.send("Hello from Arduino Controller!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        output("Received : $text")
        onMessage(text)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        onClosed()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message)
        onFailure(t)
    }

    fun output(text: String?) {
        Log.d("Websocket", text!!)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}