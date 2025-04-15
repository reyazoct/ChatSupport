package com.reyaz.chatsupport.ui.screens.userchat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyaz.chatsupport.R
import com.reyaz.chatsupport.base.UiData
import com.reyaz.chatsupport.domain.model.ChatMessageOwner
import com.reyaz.chatsupport.domain.model.UserChatMessage
import com.reyaz.chatsupport.ui.common.ErrorContent
import com.reyaz.chatsupport.ui.theme.MessageBorderColor
import com.reyaz.chatsupport.ui.theme.OtherMessageBg
import com.reyaz.chatsupport.ui.theme.UserMessageBg

@Composable
fun UserChatScreen() {
    val viewModel = viewModel<UserChatViewModel>()
    val messageListUiData by viewModel.messagesList.collectAsStateWithLifecycle()
    if (messageListUiData is UiData.Error) {
        ErrorContent(
            modifier = Modifier.fillMaxSize(),
            errorData = messageListUiData as UiData.Error<List<UserChatMessage>>,
        )
    } else {
        val messageList = messageListUiData.dataOrNull
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Bottom,
            ),
            reverseLayout = true,
        ) {
            item {
                Spacer(
                    Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .height(16.dp)
                )
            }
            items(
                messageList?.size ?: 3,
            ) {
                ChatItem(
                    userChatMessage = messageList?.getOrNull(it),
                )
            }
            item {
                Spacer(
                    Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .height(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatItem(
    modifier: Modifier = Modifier,
    userChatMessage: UserChatMessage?
) {
    val backgroundColor = when (userChatMessage?.owner) {
        ChatMessageOwner.Other -> OtherMessageBg
        ChatMessageOwner.You -> UserMessageBg
        null -> OtherMessageBg
    }
    val align = when (userChatMessage?.owner) {
        ChatMessageOwner.Other -> Alignment.CenterStart
        ChatMessageOwner.You -> Alignment.CenterEnd
        null -> Alignment.CenterStart
    }
    Box(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier
                .align(align)
                .background(
                    color = backgroundColor,
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = MessageBorderColor,
                    shape = CircleShape,
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp,
                ),
            text = userChatMessage?.message ?: stringResource(R.string.loading),
        )
    }
}