package ru.guzeevmd.activediabetesassistant.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.guzeevmd.activediabetesassistant.cards.GlucoseInfoCard
import ru.guzeevmd.activediabetesassistant.cards.GlucoseInfoCreateCard
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import ru.guzeevmd.activediabetesassistant.ui.theme.NavigationBarMediumTheme

@Composable
fun GlucoseLevelsScreen(navController: NavController) {
    val glucoseInfoSet = remember {
        mutableStateOf(setOf<GlucoseInfoViewModel>())
    }
    val exSet = remember {
        mutableStateOf(setOf<Exception>())
    }

    var showCard by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            NavigationBarMediumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(refreshTrigger) {
                        val client = DiabetesAssistantApiClient()
                        var resp: Collection<GlucoseInfoViewModel> = emptyList()
                        try {
                            resp = client.getGlucoseInfoCollection(10, 0).modelList
                            glucoseInfoSet.value = resp.toSet()
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(
                                message = e.message.toString()
                            )
                        }
                    }

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
                        LazyColumn {
                            items(glucoseInfoSet.value.size) {
                                GlucoseInfoCard(
                                    glucoseInfoSet.value.elementAt(it),
                                    paddingValues,
                                    snackbarHostState
                                ) { refreshTrigger++ }
                            }
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
                                    .background(MaterialTheme.colorScheme.background)
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            ) {
                                GlucoseInfoCreateCard(
                                    snackbarHostState = snackbarHostState,
                                    onClose = { showCard = false; refreshTrigger++ }
                                )
                            }
                        }
                    }


                    for (ex in exSet.value) {
                        Text(ex.message.toString())
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