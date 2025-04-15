package com.reyaz.chatsupport.data.socket

import android.util.Log
import com.reyaz.chatsupport.data.dto.ChatMessage
import com.reyaz.chatsupport.domain.model.ChatUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class ChatWebSocketListener(
    private val globalCoroutineScope: CoroutineScope,
) : WebSocketListener() {
    private val _newChatUpdate = MutableStateFlow<ChatUpdate>(ChatUpdate.NotConnected)
    val newChatUpdate = _newChatUpdate.asStateFlow()

    private lateinit var webSocket: WebSocket

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        this.webSocket = webSocket
        webSocket.send("Hello from Android!")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.d("WebSocket", "From: $bytes")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        globalCoroutineScope.launch {
            try {
                val chatMessage = Json.decodeFromString<ChatMessage>(text)
                _newChatUpdate.emit(
                    ChatUpdate.NewChatMessage(
                        userId = chatMessage.userId,
                        senderName = chatMessage.senderName,
                        message = chatMessage.message,
                    )
                )
                Log.d("WebSocket", "From: ${chatMessage.userId}, Msg: ${chatMessage.message}")
            } catch (e: Exception) {
                Log.e("WebSocket", "JSON parse error: ${e.message} $text")
            }
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e("WebSocket", "Error: ${t.message}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.close(1000, null)
        Log.d("WebSocket", "Closing: $code / $reason")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d("WebSocket", "Closed: $code / $reason")
    }

    fun sendMessage(chatMessage: ChatMessage): Boolean {
        return webSocket.send(Json.encodeToString(chatMessage))
    }
}