package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MoveChoice
import com.example.ui.components.MoveChoiceChip
import com.example.ui.components.SurvivorCommentCard
import com.example.ui.theme.BioGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.SilentRunViewModel

@Composable
fun CommunityMovesScreen(
    viewModel: SilentRunViewModel,
    modifier: Modifier = Modifier
) {
    val comments by viewModel.comments.collectAsState()
    var filterChoice by remember { mutableStateOf<MoveChoice?>(null) }
    var inputMoveText by remember { mutableStateOf("") }
    var inputChoice by remember { mutableStateOf(MoveChoice.HIDE) }

    val filteredComments = if (filterChoice == null) {
        comments
    } else {
        comments.filter { it.moveChoice == filterChoice }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("community_moves_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Survivor Community Moves",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tactical responses to 'Would you run or hide?' from worldwide cinema fans.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTabChip(
                    label = "All Moves",
                    selected = filterChoice == null,
                    onClick = { filterChoice = null }
                )
                FilterTabChip(
                    label = "🏃 Run (36%)",
                    selected = filterChoice == MoveChoice.RUN,
                    onClick = { filterChoice = MoveChoice.RUN }
                )
                FilterTabChip(
                    label = "🤫 Hide (64%)",
                    selected = filterChoice == MoveChoice.HIDE,
                    onClick = { filterChoice = MoveChoice.HIDE }
                )
                FilterTabChip(
                    label = "🔊 Decoy",
                    selected = filterChoice == MoveChoice.DECOY,
                    onClick = { filterChoice = MoveChoice.DECOY }
                )
            }
        }

        // Post Input Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("community_post_card"),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Submit Your Survival Strategy:",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MoveChoiceChip(
                            label = "Run 🏃",
                            selected = inputChoice == MoveChoice.RUN,
                            activeColor = WarningAmber,
                            onClick = { inputChoice = MoveChoice.RUN }
                        )
                        MoveChoiceChip(
                            label = "Hide 🤫",
                            selected = inputChoice == MoveChoice.HIDE,
                            activeColor = NeonCyan,
                            onClick = { inputChoice = MoveChoice.HIDE }
                        )
                        MoveChoiceChip(
                            label = "Decoy 🔊",
                            selected = inputChoice == MoveChoice.DECOY,
                            activeColor = BioGreen,
                            onClick = { inputChoice = MoveChoice.DECOY }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputMoveText,
                            onValueChange = { inputMoveText = it },
                            placeholder = {
                                Text(
                                    "Explain your survival move in detail...",
                                    color = TextTertiary,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("community_input_text"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = OutlineBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = NeonCyan
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputMoveText.isNotBlank()) {
                                    viewModel.addComment(inputChoice, inputMoveText)
                                    inputMoveText = ""
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .background(NeonCyan, CircleShape)
                                .testTag("community_post_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Post strategy",
                                tint = SurfaceDark
                            )
                        }
                    }
                }
            }
        }

        // List of filtered comments
        items(filteredComments) { comment ->
            SurvivorCommentCard(
                comment = comment,
                onUpvote = { viewModel.toggleUpvoteComment(comment.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FilterTabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) NeonCyan.copy(alpha = 0.2f) else SurfaceElevated,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (selected) NeonCyan else OutlineBorder)
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) NeonCyan else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
