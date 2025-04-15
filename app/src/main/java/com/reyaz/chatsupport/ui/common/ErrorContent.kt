package com.reyaz.chatsupport.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.reyaz.chatsupport.base.UiData

@Composable
fun <T> ErrorContent(
    modifier: Modifier = Modifier,
    errorData: UiData.Error<T>,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = errorData.throwable.message ?: "Something went wrong",
            style = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
            )
        )
    }
}