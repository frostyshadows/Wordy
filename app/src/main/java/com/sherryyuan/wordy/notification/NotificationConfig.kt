package com.sherryyuan.wordy.notification

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationConfig @Inject constructor() {
    val showRemoteInputWarningFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
}
