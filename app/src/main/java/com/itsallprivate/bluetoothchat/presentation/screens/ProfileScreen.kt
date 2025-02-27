package com.itsallprivate.bluetoothchat.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.itsallprivate.bluetoothchat.presentation.components.ProfilePicture
import com.itsallprivate.bluetoothchat.presentation.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    val profile by viewModel.profile.collectAsState()
    var name by rememberSaveable { viewModel.name }

    var isEditing by rememberSaveable { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        if (isEditing) {
                            Box(modifier = Modifier.padding(start = 16.dp)) {
                                TextButton(
                                    onClick = {
                                        viewModel.reset()
                                        isEditing = false
                                    },
                                ) {
                                    Text("Cancel")
                                }
                            }
                        } else {
                            IconButton(
                                onClick = navController::popBackStack,
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = "Back",
                                )
                            }
                        }
                    }
                    Row {
                        if (isEditing) {
                            TextButton(
                                onClick = {
                                    viewModel.save()
                                    isEditing = false
                                },
                            ) {
                                Text("Save")
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    isEditing = !isEditing
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Back",
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ProfilePicture(
                        name = profile.name,
                        size = 186,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Name", style = MaterialTheme.typography.titleLarge)
                        if (isEditing) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                            )
                        } else {
                            Text(profile.name)
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Device Name", style = MaterialTheme.typography.titleLarge)
                        Text(profile.name)
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Address", style = MaterialTheme.typography.titleLarge)
                        Text(profile.address)
                    }
                }
            }
        }
    }
}
