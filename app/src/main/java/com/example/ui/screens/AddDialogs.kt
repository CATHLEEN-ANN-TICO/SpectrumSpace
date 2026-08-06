package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AddCustomCardDialog(
    onAddCard: (title: String, phrase: String, category: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var phrase by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Needs") }

    val categories = listOf("Needs", "Emotions", "Answers", "Custom")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_card_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add Custom Communication Card",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Card Title (Short)") },
                    placeholder = { Text("E.g., Need Headphones") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_card_title"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = phrase,
                    onValueChange = { phrase = it },
                    label = { Text("Full Phrase to Speak or Show") },
                    placeholder = { Text("E.g., I need my noise canceling headphones right now.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_card_phrase"),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "Category:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && phrase.isNotBlank()) {
                                onAddCard(title.trim(), phrase.trim(), category)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && phrase.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_card_button")
                    ) {
                        Text("Save Card")
                    }
                }
            }
        }
    }
}

@Composable
fun AddRoutineStepDialog(
    defaultCategory: String,
    onAddStep: (category: String, title: String, description: String, durationMinutes: Int, iconName: String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        com.example.ui.components.VisualRoutineBuilderCard(
            defaultCategory = defaultCategory,
            onSaveRoutineStep = { category, title, description, iconName, colorTag, durationMinutes ->
                onAddStep(category, title, description, durationMinutes, iconName)
                onDismiss()
            },
            onCancel = onDismiss
        )
    }
}
