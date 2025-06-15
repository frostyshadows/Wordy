package com.sherryyuan.wordy.screens.projectdetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sherryyuan.wordy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailTopAppBar(
    projectTitle: String,
    isEditing: Boolean,
    onIsEditingChange: (Boolean) -> Unit,
    onTitleUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editedTitle by remember(projectTitle) {
        mutableStateOf(projectTitle)
    }
    TopAppBar(
        modifier = modifier,
        title = {
            AnimatedContent(
                targetState = isEditing,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220, delayMillis = 60)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(220, delayMillis = 60)
                            ))
                        .togetherWith(fadeOut(animationSpec = tween(120)))
                },
            ) { animatedIsEditing ->
                if (animatedIsEditing) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp),
                        value = editedTitle,
                        singleLine = true,
                        onValueChange = { editedTitle = it },
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp),
                        textAlign = TextAlign.Center,
                        text = projectTitle,
                    )

                }
            }
        },
        actions = {
            if (isEditing) {
                IconButton(
                    onClick = {
                        onTitleUpdate(editedTitle)
                        onIsEditingChange(false)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Outlined.Check,
                        contentDescription = stringResource(R.string.save_label),
                    )
                }
            } else {
                IconButton(
                    onClick = { onIsEditingChange(true) }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit_label),
                    )
                }
            }
        },
    )
}
