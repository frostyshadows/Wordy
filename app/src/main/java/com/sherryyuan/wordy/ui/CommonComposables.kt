package com.sherryyuan.wordy.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpacer(heightDp: Int = 24) =
    Spacer(modifier = Modifier.padding(vertical = heightDp.dp))

@Composable
fun HorizontalSpacer(widthDp: Int = 12) =
    Spacer(modifier = Modifier.padding(horizontal = widthDp.dp))
