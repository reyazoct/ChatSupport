package com.reyaz.chatsupport.ui.screens.chatlist

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.reyaz.chatsupport.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyaz.chatsupport.base.UiData
import com.reyaz.chatsupport.domain.model.ChatPreview
import com.reyaz.chatsupport.ui.common.ErrorContent
import com.reyaz.chatsupport.ui.theme.DefaultIconBg
import com.reyaz.chatsupport.ui.theme.DefaultIconColor
import com.reyaz.chatsupport.ui.theme.TextColorPrimary
import com.reyaz.chatsupport.ui.theme.TextColorSecondary

@Composable
fun ChatListScreen(
    onChatClick: (Int) -> Unit,
) {
    val viewModel = viewModel<ChatListViewModel>()
    val chatPreviewUiData by viewModel.chatPreviewList.collectAsStateWithLifecycle()
    if (chatPreviewUiData is UiData.Error) {
        ErrorContent(
            modifier = Modifier.fillMaxSize(),
            errorData = chatPreviewUiData as UiData.Error<List<ChatPreview>>,
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            item {
                Spacer(
                    Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .height(12.dp)
                )
            }
            items(
                count = chatPreviewUiData.dataOrNull?.size ?: 3,
                key = { index -> chatPreviewUiData.dataOrNull?.getOrNull(index)?.userId ?: index },
            ) { index ->
                val chatPreview = chatPreviewUiData.dataOrNull?.getOrNull(index)
                ChatListItem(
                    modifier = Modifier
                        .fillMaxWidth(),
                    chatPreview = chatPreview,
                    onClick = {
                        chatPreview ?: return@ChatListItem
                        onChatClick(chatPreview.userId)
                    },
                )
            }
        }
    }
}

@Composable
private fun ChatListItem(
    modifier: Modifier = Modifier,
    chatPreview: ChatPreview?,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(
                color = DefaultIconBg,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = DefaultIconColor,
                    shape = CircleShape,
                )
        )
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1F),
        ) {
            Text(
                text = chatPreview?.userDisplayName ?: "",
                style = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextColorPrimary,
                )
            )
            val lastMessage = chatPreview?.lastMessage
            Text(
                text = lastMessage ?: stringResource(R.string.tap_to_chat),
                style = LocalTextStyle.current.copy(
                    color = TextColorSecondary,
                )
            )
        }
    }
}