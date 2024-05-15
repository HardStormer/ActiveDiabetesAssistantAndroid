package ru.guzeevmd.activediabetesassistant.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.guzeevmd.activediabetesassistant.cards.GlucoseInfoCard
import ru.guzeevmd.activediabetesassistant.cards.GlucoseInfoCreateCard
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import ru.guzeevmd.activediabetesassistant.ui.theme.NavigationBarMediumTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GlucoseLevelsScreen(navController: NavController, authToken: String) {
    val glucoseInfoSet = remember { mutableStateOf(listOf<GlucoseInfoViewModel>()) }
    val exSet = remember { mutableStateOf(setOf<Exception>()) }
    val isLoading = remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    var showCard by remember { mutableStateOf(false) }
    var isDataGetted by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val client = DiabetesAssistantApiClient(authToken)

    LaunchedEffect(refreshTrigger) {
        isLoading.value = true
        var resp: Collection<GlucoseInfoViewModel> = emptyList()
        try {
            resp = client.getGlucoseInfoCollection(10, glucoseInfoSet.value.size).modelList
            glucoseInfoSet.value = glucoseInfoSet.value + resp
            isDataGetted = true
        } catch (e: Exception) {
            exSet.value = exSet.value + e
        } finally {
            isLoading.value = false
            isRefreshing = false
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { index -> index == glucoseInfoSet.value.size - 1 && !isLoading.value }
            .distinctUntilChanged()
            .collect {
                coroutineScope.launch {
                    delay(300) // Debounce to prevent multiple triggers
                    refreshTrigger++
                }
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            NavigationBarMediumTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onVerticalDrag = { _, _ -> },
                                onDragEnd = {
                                    if (!isRefreshing) {
                                        isRefreshing = true
                                        glucoseInfoSet.value =
                                            emptyList() // Clear the list before refreshing
                                        refreshTrigger++
                                    }
                                }
                            )
                        },
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isDataGetted) {
                        if (glucoseInfoSet.value.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Нет записей")
                            }
                        } else {
                            LazyColumn(state = listState) {
                                items(glucoseInfoSet.value.size, key = { it }) { index ->
                                    GlucoseInfoCard(
                                        glucoseInfoSet.value[index],
                                        paddingValues,
                                        snackbarHostState,
                                        authToken
                                    ) { refreshTrigger++ }
                                }
                                item {
                                    if (isLoading.value) {
                                        Column {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .padding(vertical = 20.dp)
                                                    .align(Alignment.CenterHorizontally)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showCard,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    onClick = { showCard = false },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() })
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .imePadding()
                            ) {
                                GlucoseInfoCreateCard(
                                    snackbarHostState = snackbarHostState,
                                    onClose = { showCard = false; refreshTrigger++ },
                                    authToken = authToken
                                )
                            }
                        }
                    }

                    exSet.value.forEach { ex ->
                        LaunchedEffect(snackbarHostState) {
                            snackbarHostState.showSnackbar(message = ex.message ?: "Unknown error")
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (!showCard) {
                            FloatingActionButton(
                                onClick = { showCard = true },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.background,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.BottomEnd)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add")
                            }
                        }
                    }
                }
            }
        }
    )
}
