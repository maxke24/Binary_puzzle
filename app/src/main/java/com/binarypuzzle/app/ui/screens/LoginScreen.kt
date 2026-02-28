package com.binarypuzzle.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binarypuzzle.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(user) {
        if (user != null) onLoginSuccess()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result.data)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Binary Puzzle",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Train your logical thinking",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Decorative binary grid preview
        BinaryPreviewGrid()

        Spacer(modifier = Modifier.height(48.dp))

        error?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                viewModel.clearError()
                launcher.launch(viewModel.getSignInIntent())
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign in with Google", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sign in to save your progress across devices",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BinaryPreviewGrid() {
    // A decorative, valid 6x6 binary puzzle solution
    val pattern = listOf(
        listOf(0, 1, 0, 0, 1, 1),
        listOf(1, 0, 1, 1, 0, 0),
        listOf(0, 0, 1, 0, 1, 1),
        listOf(1, 1, 0, 1, 0, 0),
        listOf(1, 0, 0, 1, 1, 0),
        listOf(0, 1, 1, 0, 0, 1),
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        pattern.forEach { row ->
            Row {
                row.forEach { cell ->
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .padding(2.dp),
                        shape = MaterialTheme.shapes.small,
                        color = if (cell == 0)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = cell.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (cell == 0)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
