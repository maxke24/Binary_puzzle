package com.binarypuzzle.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binarypuzzle.app.data.model.UserProgress
import com.binarypuzzle.app.viewmodel.ProgressViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    progressViewModel: ProgressViewModel,
    onBack: () -> Unit
) {
    val progressList by progressViewModel.progressList.collectAsState()
    val stats by progressViewModel.stats.collectAsState()
    val bestTimes by progressViewModel.bestTimes.collectAsState()
    val isLoading by progressViewModel.isLoading.collectAsState()
    val error by progressViewModel.error.collectAsState()

    val completed = progressList.filter { it.completed }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Progress") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        StatsOverview(stats = stats, bestTimes = bestTimes)
                    }

                    if (completed.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No completed puzzles yet.\nStart playing to track your progress!",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Completed Puzzles (${completed.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(completed) { progress ->
                            ProgressItem(progress = progress)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsOverview(stats: Map<String, Int>, bestTimes: Map<String, Long>) {
    val total = stats.values.sum()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Total Solved: $total",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val difficulties = listOf(
                    Triple("Easy", "EASY", MaterialTheme.colorScheme.primaryContainer),
                    Triple("Medium", "MEDIUM", MaterialTheme.colorScheme.secondaryContainer),
                    Triple("Hard", "HARD", MaterialTheme.colorScheme.tertiaryContainer),
                )
                difficulties.forEach { (label, key, color) ->
                    StatPill(
                        label = label,
                        count = stats[key] ?: 0,
                        bestTime = bestTimes[key],
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    count: Int,
    bestTime: Long?,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(color = color, shape = MaterialTheme.shapes.medium) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp)
            if (bestTime != null) {
                Text(
                    text = formatTime(bestTime),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text("--:--", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ProgressItem(progress: UserProgress) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy  HH:mm", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = progress.difficulty.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (progress.completedAtMillis > 0) {
                    Text(
                        text = dateFormat.format(Date(progress.completedAtMillis)),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = formatTime(progress.timeSeconds),
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
