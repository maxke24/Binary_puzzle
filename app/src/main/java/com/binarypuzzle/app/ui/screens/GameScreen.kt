package com.binarypuzzle.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binarypuzzle.app.game.BinaryPuzzle
import com.binarypuzzle.app.viewmodel.AuthViewModel
import com.binarypuzzle.app.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    difficulty: BinaryPuzzle.Difficulty,
    gameViewModel: GameViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val puzzle by gameViewModel.puzzle.collectAsState()
    val grid by gameViewModel.currentGrid.collectAsState()
    val invalidCells by gameViewModel.invalidCells.collectAsState()
    val isSolved by gameViewModel.isSolved.collectAsState()
    val elapsedSeconds by gameViewModel.elapsedSeconds.collectAsState()
    val user by authViewModel.user.collectAsState()

    // Start a fresh puzzle when first entering this screen
    LaunchedEffect(difficulty) {
        gameViewModel.startNewPuzzle(difficulty)
    }

    // Auto-save on solve
    LaunchedEffect(isSolved) {
        if (isSolved) {
            val uid = user?.uid ?: return@LaunchedEffect
            val name = user?.displayName ?: ""
            gameViewModel.saveCompletedPuzzle(uid, name)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${difficulty.displayName} Puzzle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = formatTime(elapsedSeconds),
                        modifier = Modifier.padding(end = 8.dp),
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { gameViewModel.startNewPuzzle(difficulty) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "New Puzzle")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (puzzle == null || grid.isEmpty()) {
                CircularProgressIndicator()
            } else if (isSolved) {
                WinScreen(
                    elapsedSeconds = elapsedSeconds,
                    difficulty = difficulty,
                    onNewPuzzle = { gameViewModel.startNewPuzzle(difficulty) },
                    onBack = onBack
                )
            } else {
                Text(
                    text = "Tap to cycle: empty \u2192 0 \u2192 1",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                PuzzleGrid(
                    grid = grid,
                    isGiven = puzzle!!.isGiven,
                    invalidCells = invalidCells,
                    size = puzzle!!.size,
                    onCellClick = { r, c -> gameViewModel.cellTapped(r, c) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                RulesHint()
            }
        }
    }
}

@Composable
fun PuzzleGrid(
    grid: Array<IntArray>,
    isGiven: Array<BooleanArray>,
    invalidCells: Set<Pair<Int, Int>>,
    size: Int,
    onCellClick: (Int, Int) -> Unit
) {
    val cellSize: Dp = when (size) {
        6 -> 52.dp
        8 -> 42.dp
        else -> 34.dp
    }
    val fontSize = when (size) {
        6 -> 20.sp
        8 -> 16.sp
        else -> 13.sp
    }

    Column {
        for (r in 0 until size) {
            Row {
                for (c in 0 until size) {
                    val value = grid[r][c]
                    val given = isGiven[r][c]
                    val isInvalid = Pair(r, c) in invalidCells

                    val bgColor = when {
                        isInvalid -> MaterialTheme.colorScheme.errorContainer
                        value == 0 && given -> MaterialTheme.colorScheme.primaryContainer
                        value == 0 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        value == 1 && given -> MaterialTheme.colorScheme.secondaryContainer
                        value == 1 -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    val textColor = when {
                        isInvalid -> MaterialTheme.colorScheme.onErrorContainer
                        value == 0 -> MaterialTheme.colorScheme.onPrimaryContainer
                        value == 1 -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(2.dp)
                            .background(
                                color = bgColor,
                                shape = MaterialTheme.shapes.small
                            )
                            .then(
                                if (!given) Modifier.clickable { onCellClick(r, c) }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (value != -1) {
                            Text(
                                text = value.toString(),
                                fontSize = fontSize,
                                fontWeight = if (given) FontWeight.Bold else FontWeight.Normal,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WinScreen(
    elapsedSeconds: Long,
    difficulty: BinaryPuzzle.Difficulty,
    onNewPuzzle: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Puzzle Solved!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Time: ${formatTime(elapsedSeconds)}",
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "Difficulty: ${difficulty.displayName}",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNewPuzzle, modifier = Modifier.fillMaxWidth()) {
            Text("Play Again (${difficulty.displayName})")
        }

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Menu")
        }
    }
}

@Composable
private fun RulesHint() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Rules",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("\u2022 Equal 0s and 1s in each row & column", fontSize = 12.sp)
            Text("\u2022 No 3 consecutive identical digits", fontSize = 12.sp)
            Text("\u2022 All rows and columns must be unique", fontSize = 12.sp)
        }
    }
}

fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
