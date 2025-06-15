package com.sherryyuan.wordy.screens.projectdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.screens.projectslist.getStatusColor
import com.sherryyuan.wordy.screens.projectslist.getStatusLabelRes
import com.sherryyuan.wordy.ui.HorizontalSpacer

@Composable
fun ProjectStatusRow(
    status: ProjectStatus,
    isEditing: Boolean,
    onStatusUpdate: (ProjectStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEditingStatus by remember(isEditing) { mutableStateOf(false) }
    val statusChipHeight = measureTextHeight(
        stringResource(getStatusLabelRes(status)),
        LocalTextStyle.current,
    ) + 4.dp
    Row(modifier = modifier.heightIn(min = statusChipHeight)) {
        Text(stringResource(R.string.project_detail_status_label))
        HorizontalSpacer(4)
        if (isEditingStatus) {
            Box(modifier = Modifier.wrapContentSize()) {
                DropdownMenu(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    expanded = isEditingStatus,
                    onDismissRequest = { isEditingStatus = false }
                ) {
                    StatusChip(
                        status = status,
                        onClick = { isEditingStatus = false }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                    )
                    ProjectStatus.entries
                        .filter { it != status }
                        .forEach { status ->
                            StatusChip(
                                modifier = Modifier.padding(top = 4.dp),
                                status = status,
                                onClick = {
                                    isEditingStatus = false
                                    onStatusUpdate(status)
                                }
                            )
                        }
                }
            }
        } else {
            StatusChip(
                status = status,
                onClick = if (isEditing) {
                    { isEditingStatus = true }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
private fun StatusChip(
    status: ProjectStatus,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Text(
        modifier = modifier
            .background(
                color = getStatusColor(status),
                shape = RoundedCornerShape(percent = 50),
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .then(
                onClick?.let { Modifier.clickable { it() } }
                    ?: Modifier
            ),
        text = stringResource(getStatusLabelRes(status))
    )
}

@Composable
private fun measureTextHeight(text: String, style: TextStyle): Dp {
    val textMeasurer = rememberTextMeasurer()
    val heightPixels = textMeasurer.measure(text, style).size.height
    return with(LocalDensity.current) { heightPixels.toDp() }
}
