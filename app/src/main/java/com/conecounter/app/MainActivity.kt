package com.conecounter.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.conecounter.app.shortcuts.ShortcutHelper
import com.conecounter.app.ui.AppRoot
import com.conecounter.app.ui.CounterViewModel
import com.conecounter.app.ui.theme.ConeCounterTheme

class MainActivity : ComponentActivity() {

    sealed class PendingAction {
        data class QuickLog(val kidId: Long) : PendingAction()
        object OpenPicker : PendingAction()
    }

    private val viewModel: CounterViewModel by viewModels {
        CounterViewModel.factory(application as ConeCounterApp)
    }

    private var pendingAction by mutableStateOf<PendingAction?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            ConeCounterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(
                        viewModel = viewModel,
                        pendingAction = pendingAction,
                        onPendingActionHandled = { pendingAction = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            ShortcutHelper.ACTION_QUICK_LOG -> {
                val kidId = intent.getLongExtra(ShortcutHelper.EXTRA_KID_ID, -1L)
                if (kidId != -1L) pendingAction = PendingAction.QuickLog(kidId)
            }
            ShortcutHelper.ACTION_QUICK_LOG_PICKER -> {
                pendingAction = PendingAction.OpenPicker
            }
        }
    }
}
