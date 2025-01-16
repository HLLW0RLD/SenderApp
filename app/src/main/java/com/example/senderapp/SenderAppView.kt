package com.example.senderapp

import android.content.ContentValues
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun SenderAppPreview() {
    SenderAppView("test")
}

@Composable
fun SenderAppView(
    receivedMessage: String
) {
    val context = LocalContext.current
    val messageToSend = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(stringResource(R.string.received))
                }
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append(receivedMessage)
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Divider(Modifier.padding(vertical = 4.dp))

        Row(
            modifier = Modifier.background(Color.LightGray).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    ),
                value = messageToSend.value,
                onValueChange = { messageToSend.value = it },
                label = { Text(stringResource(R.string.set_text)) },
            )
            Button(
                modifier = Modifier.padding(4.dp),
                border = BorderStroke(1.dp, Color.Red),
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red, ),
                onClick = {
                    val contentValues = ContentValues().apply { put("data", messageToSend.value) }
                    context.contentResolver.insert(SecureDataProvider.CONTENT_URI, contentValues)
                }
            ) {
                Text(text = stringResource(R.string.send), fontSize = 14.sp)
            }
        }
    }
}