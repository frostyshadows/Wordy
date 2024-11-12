package com.sherryyuan.wordy.ui.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionSpacer(heightDp: Int = 24) =
    Spacer(modifier = Modifier.padding(vertical = heightDp.dp))
