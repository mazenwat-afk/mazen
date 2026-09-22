package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AppCurrency
import com.example.data.model.TransactionType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen

@Composable
fun AddTransactionDialog(
    type: TransactionType,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, title: String) -> Unit
) {
    val isIncome = type == TransactionType.INCOME
    val accentColor = if (isIncome) IncomeGreen else ExpenseRed
    val dialogTitle = if (isIncome) "إضافة وارد (+)" else "إضافة منصرف (-)"

    var amountText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }
    var isAmountError by remember { mutableStateOf(false) }

    val amountFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        amountFocusRequester.requestFocus()
    }

    val submitAction = {
        val parsed = amountText.toDoubleOrNull() ?: 0.0
        if (parsed <= 0) {
            isAmountError = true
        } else {
            val statement = if (titleText.isNotBlank()) titleText.trim() else (if (isIncome) "وارد" else "منصرف")
            onConfirm(parsed, statement)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // المبلغ (Amount)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || it == '.' }) {
                            amountText = input
                            if (isAmountError && (input.toDoubleOrNull() ?: 0.0) > 0) {
                                isAmountError = false
                            }
                        }
                    },
                    placeholder = { Text("المبلغ") },
                    trailingIcon = {
                        VoiceInputButton(
                            onSpeechResult = { spoken ->
                                // Extract numbers / digits or decimal from speech
                                val digitsOnly = spoken.filter { it.isDigit() || it == '.' }
                                if (digitsOnly.isNotBlank()) {
                                    amountText = digitsOnly
                                    isAmountError = false
                                }
                            },
                            tint = accentColor,
                            testTag = "voice_input_dialog_amount"
                        )
                    },
                    suffix = {
                        Text(
                            text = currency.symbolArabic,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    },
                    isError = isAmountError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(amountFocusRequester)
                        .testTag("tx_dialog_amount_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // البيان / الملاحظة
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    placeholder = { Text("البيان / الملاحظة (اختياري)") },
                    trailingIcon = {
                        VoiceInputButton(
                            onSpeechResult = { spoken ->
                                titleText = spoken
                            },
                            tint = accentColor,
                            testTag = "voice_input_dialog_title"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submitAction() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_dialog_title_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = submitAction,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier.testTag("submit_tx_dialog_btn")
            ) {
                Text(
                    text = "حفظ",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("cancel_tx_dialog_btn")
            ) {
                Text("إلغاء")
            }
        }
    )
}
