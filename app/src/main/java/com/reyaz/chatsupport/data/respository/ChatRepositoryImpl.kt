package com.reyaz.chatsupport.data.respository

import com.reyaz.chatsupport.data.dto.ChatMessage
import com.reyaz.chatsupport.data.socket.ChatWebSocketListener
import com.reyaz.chatsupport.domain.model.ChatMessageOwner
import com.reyaz.chatsupport.domain.model.ChatMessageStatus
import com.reyaz.chatsupport.domain.model.ChatUpdate
import com.reyaz.chatsupport.domain.model.ChatUser
import com.reyaz.chatsupport.domain.model.UserChatMessage
import com.reyaz.chatsupport.domain.repository.ChatRepository
import com.reyaz.chatsupport.system.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin

class ChatRepositoryImpl(
    private val webSocketListener: ChatWebSocketListener,
    private val globalCoroutineScope: CoroutineScope,
//    private val networkMonitor: NetworkMonitor,
) : ChatRepository {

    private val networkMonitor: NetworkMonitor by getKoin().inject()

    private val _chatUserList = MutableStateFlow(createChatUserList())
    override val chatUserList: Flow<List<ChatUser>>
        get() = _chatUserList.asStateFlow()

    private val failedChatMessage = mutableListOf<Pair<Int, ChatMessage>>()

    init {
        setupCollectors()
    }

    private fun setupCollectors() {
        globalCoroutineScope.launch {
            webSocketListener.newChatUpdate.collectLatest {
                when (it) {
                    is ChatUpdate.NewChatMessage -> {
                        updateNewChatMessage(it)
                    }

                    ChatUpdate.NotConnected -> {

                    }
                }
            }
        }
        globalCoroutineScope.launch {
            networkMonitor.start()
            networkMonitor.isConnected.collectLatest {
                if (it) {
                    webSocketListener.startService()
                    trySendingAgain()
                }
            }
        }
    }

    private suspend fun trySendingAgain() {
        if (failedChatMessage.isEmpty()) return
        val (userId, chatMessage) = failedChatMessage.first()
        val succeeded = webSocketListener.sendMessage(chatMessage)
        if (succeeded) {
            failedChatMessage.removeFirstOrNull()
            updateMessageStatus(userId, chatMessage, true)
            trySendingAgain()
        }
    }

    private suspend fun updateNewChatMessage(message: ChatUpdate.NewChatMessage) {
        val oldList = _chatUserList.value.toMutableList()
        val chatUser = oldList.firstOrNull {
            message.userId == it.userId
        }
        val userChatMessage = UserChatMessage(
            message = message.message,
            owner = ChatMessageOwner.Other,
            status = ChatMessageStatus.Success,
        )
        if (chatUser == null) {
            oldList.add(
                ChatUser(
                    userId = message.userId,
                    senderName = message.senderName,
                    messages = listOf(userChatMessage)
                )
            )
        } else {
            val messagesList = chatUser.messages.toMutableList()
            messagesList.add(0, userChatMessage)
            val newChatUser = chatUser.copy(messages = messagesList)
            oldList.remove(chatUser)
            oldList.add(0, newChatUser)
        }
        _chatUserList.emit(oldList)
    }

    override fun getChatUpdates(): Flow<ChatUpdate> {
        return webSocketListener.newChatUpdate
    }

    override fun getChatMessagesByUserId(userId: Int): Flow<List<UserChatMessage>> {
        return _chatUserList.map { chatUser ->
            chatUser.firstOrNull { it.userId == userId }?.messages ?: emptyList()
        }
    }

    override fun sendMessage(userId: Int, chatMessage: ChatMessage) {
        globalCoroutineScope.launch {
            addToChatMessageList(userId, chatMessage)
            val succeeded = webSocketListener.sendMessage(chatMessage)
            updateMessageStatus(userId, chatMessage, succeeded)
        }
    }

    private suspend fun updateMessageStatus(userId: Int, chatMessage: ChatMessage, succeeded: Boolean) {
        if (!succeeded) {
            failedChatMessage.add(userId to chatMessage)
        }
        val oldList = _chatUserList.value.toMutableList()
        val chatUser = oldList.first {
            userId == it.userId
        }
        val messagesList = chatUser.messages.toMutableList()
        val userChatMessageIndex = messagesList.indexOfFirst { it.message == chatMessage.message }
        val userChatMessage = messagesList[userChatMessageIndex]
        messagesList.removeAt(userChatMessageIndex)
        val newUserChatMessage = userChatMessage.copy(status = if (succeeded) ChatMessageStatus.Success else ChatMessageStatus.Failed)
        messagesList.add(userChatMessageIndex, newUserChatMessage)

        val newChatUser = chatUser.copy(messages = messagesList)
        oldList.remove(chatUser)
        oldList.add(0, newChatUser)
        _chatUserList.emit(oldList)
    }

    private suspend fun addToChatMessageList(userId: Int, chatMessage: ChatMessage) {
        val oldList = _chatUserList.value.toMutableList()
        val chatUser = oldList.first {
            userId == it.userId
        }

        val userChatMessage = UserChatMessage(
            message = chatMessage.message,
            owner = ChatMessageOwner.You,
            status = ChatMessageStatus.Sending,
        )

        val messagesList = chatUser.messages.toMutableList()
        messagesList.add(0, userChatMessage)

        val newChatUser = chatUser.copy(messages = messagesList)
        oldList.remove(chatUser)
        oldList.add(0, newChatUser)
        _chatUserList.emit(oldList)
    }

    private fun createChatUserList(): List<ChatUser> {
        return listOf(
            ChatUser(
                userId = 1,
                senderName = "Support Bot",
                messages = emptyList(),
            ),
            ChatUser(
                userId = 2,
                senderName = "Sales Bot",
                messages = emptyList(),
            ),
            ChatUser(
                userId = 3,
                senderName = "FAQ Bot",
                messages = emptyList(),
            ),
        )

    }
}