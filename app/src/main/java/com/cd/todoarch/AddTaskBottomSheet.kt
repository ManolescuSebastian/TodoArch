package com.cd.todoarch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskBottomSheet(
    onSave: (String, String) -> Unit,
    dialogTitle: String = "Add New Task",
    titleText: String = "",
    descriptionText: String = "",
) {
    var title by remember { mutableStateOf(TextFieldValue(titleText)) }
    var description by remember { mutableStateOf(TextFieldValue(descriptionText)) }

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text(dialogTitle, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSave(title.text, description.text) }, modifier = Modifier.fillMaxWidth()) {
            Text("Save")
        }
    }
}
