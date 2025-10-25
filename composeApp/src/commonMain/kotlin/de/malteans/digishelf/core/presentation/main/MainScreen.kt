package de.malteans.digishelf.core.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreenRoot(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action) }
    )
}

@Composable
fun MainScreen(
    state: MainState,
    onAction: (MainAction) -> Unit
) {

}