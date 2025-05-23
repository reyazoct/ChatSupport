package com.reyaz.chatsupport.ui.screens.userchat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyaz.chatsupport.R
import com.reyaz.chatsupport.base.UiData
import com.reyaz.chatsupport.domain.model.ChatMessageOwner
import com.reyaz.chatsupport.domain.model.UserChatMessage
import com.reyaz.chatsupport.ui.common.ErrorContent
import com.reyaz.chatsupport.ui.theme.DefaultIconBg
import com.reyaz.chatsupport.ui.theme.MessageBorderColor
import com.reyaz.chatsupport.ui.theme.OtherMessageBg
import com.reyaz.chatsupport.ui.theme.PrimaryColor
import com.reyaz.chatsupport.ui.theme.TextColorSecondary
import com.reyaz.chatsupport.ui.theme.UserMessageBg

@Composable
fun UserChatScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        val viewModel = viewModel<UserChatViewModel>()
        val messageListUiData by viewModel.messagesList.collectAsStateWithLifecycle()
        val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()

        if (messageListUiData is UiData.Error) {
            ErrorContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                errorData = messageListUiData as UiData.Error<List<UserChatMessage>>,
            )
        } else {
            val messageList = messageListUiData.dataOrNull
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.Bottom,
                ),
                reverseLayout = true,
            ) {
                if (!isConnected) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.statusBars),
                            text = stringResource(R.string.no_internet_connection),
                            style = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                            )
                        )
                    }
                }

                items(
                    messageList?.size ?: 3,
                ) {
                    ChatItem(
                        modifier = Modifier.fillMaxWidth(),
                        userChatMessage = messageList?.getOrNull(it),
                    )
                }

                item {
                    Spacer(
                        Modifier
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .height(16.dp)
                    )
                }
            }
        }
        val textFieldValue by viewModel.textFieldValue.collectAsStateWithLifecycle()
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = textFieldValue,
            onValueChange = viewModel::onTextChange,
            maxLines = 1,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .border(
                            width = 1.dp,
                            color = MessageBorderColor,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(1F)
                    ) {
                        innerTextField()
                    }
                    val alphaSend by remember {
                        derivedStateOf { if (textFieldValue.text.isEmpty()) 0F else 1F }
                    }

                    Icon(
                        modifier = Modifier
                            .alpha(alphaSend)
                            .size(32.dp)
                            .background(
                                color = DefaultIconBg,
                                shape = CircleShape,
                            )
                            .clip(CircleShape)
                            .clickable(onClick = viewModel::sendMessage)
                            .padding(8.dp),
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        tint = PrimaryColor,
                        contentDescription = null,
                    )
                }
            }
        )
        Spacer(
            Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(16.dp)
        )
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
        ChatMessageOwner.Other -> Alignment.Start
        ChatMessageOwner.You -> Alignment.End
        null -> Alignment.Start
    }
    Column(
        modifier = modifier,
        horizontalAlignment = align,
    ) {
        Text(
            modifier = Modifier
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
        val status = userChatMessage?.status?.displayMessageResId?.let { stringResource(it) }
        if (userChatMessage?.owner == ChatMessageOwner.You && status != null) {
            Text(
                text = status,
                style = LocalTextStyle.current.copy(
                    color = TextColorSecondary,
                    fontSize = 10.sp
                )
            )
        }
    }
}