package com.binarypuzzle.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binarypuzzle.app.game.BinaryPuzzle
import com.binarypuzzle.app.viewmodel.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    progressViewModel: ProgressViewModel,
    onStartGame: (BinaryPuzzle.Difficulty) -> Unit,
    onViewProgress: () -> Unit
) {
    val stats by progressViewModel.stats.collectAsState()
    val bestTimes by progressViewModel.bestTimes.collectAsState()

    LaunchedEffect(Unit) {
        progressViewModel.loadProgress()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Binary Puzzle") },
                actions = {
                    IconButton(onClick = onViewProgress) {
                        Icon(Icons.Default.BarChart, contentDescription = "View Progress")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Session stats card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Session Progress",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Progress resets when you close the app",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BinaryPuzzle.Difficulty.entries.forEach { diff ->
                            StatItem(
                                label = diff.displayName,
                                count = stats[diff.name] ?: 0,
                                bestTime = bestTimes[diff.name]
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose Difficulty",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            BinaryPuzzle.Difficulty.entries.forEach { difficulty ->
                DifficultyCard(difficulty = difficulty, onClick = { onStartGame(difficulty) })
            }
        }
    }
}

@Composable
private fun StatItem(label: String, count: Int, bestTime: Long?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (bestTime != null) {
            Text(
                text = "Best: ${formatTime(bestTime)}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun DifficultyCard(difficulty: BinaryPuzzle.Difficulty, onClick: () -> Unit) {
    val (containerColor, contentColor, description) = when (difficulty) {
        BinaryPuzzle.Difficulty.EASY -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "${difficulty.gridSize}\u00d7${difficulty.gridSize} grid  \u00b7  Perfect for beginners"
        )
        BinaryPuzzle.Difficulty.MEDIUM -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "${difficulty.gridSize}\u00d7${difficulty.gridSize} grid  \u00b7  A real challenge"
        )
        BinaryPuzzle.Difficulty.HARD -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "${difficulty.gridSize}\u00d7${difficulty.gridSize} grid  \u00b7  For experts only"
        )
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = difficulty.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = contentColor
                )
            }
            Text(text = "\u25b6", fontSize = 22.sp, color = contentColor)
        }
    }
}
