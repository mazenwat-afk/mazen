package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.PersonEntity
import com.example.data.model.AppCurrency
import com.example.data.model.TransactionType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    initialType: TransactionType,
    currency: AppCurrency,
    persons: List<PersonEntity>,
    preselectedPersonId: Long?,
    onDismiss: () -> Unit,
    onSave: (personId: Long, personName: String, title: String, amount: Double, type: TransactionType, note: String) -> Unit,
    onAddNewPersonRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedType by remember { mutableStateOf(initialType) }
    var selectedPerson by remember {
        mutableStateOf(
            persons.firstOrNull { it.id == preselectedPersonId }
                ?: persons.firstOrNull()
        )
    }
    var isPersonDropdownExpanded by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var personError by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("add_transaction_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            Text(
                text = "تسجيل عملية جديدة",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Transaction Type Selector: [ وارد ] [ منصرف ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val isIncomeSelected = selectedType == TransactionType.INCOME
                val isExpenseSelected = selectedType == TransactionType.EXPENSE

                val incomeBg by animateColorAsState(
                    targetValue = if (isIncomeSelected) IncomeGreen else IncomeGreen.copy(alpha = 0.08f),
                    label = "income_bg"
                )
                val incomeText by animateColorAsState(
                    targetValue = if (isIncomeSelected) Color.White else IncomeGreen,
                    label = "income_text"
                )

                Surface(
                    onClick = { selectedType = TransactionType.INCOME },
                    shape = RoundedCornerShape(16.dp),
                    color = incomeBg,
                    border = BorderStroke(
                        width = if (isIncomeSelected) 2.dp else 1.dp,
                        color = IncomeGreen.copy(alpha = if (isIncomeSelected) 1f else 0.3f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("type_income_toggle")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = incomeText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "وارد",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = incomeText
                        )
                    }
                }

                val expenseBg by animateColorAsState(
                    targetValue = if (isExpenseSelected) ExpenseRed else ExpenseRed.copy(alpha = 0.08f),
                    label = "expense_bg"
                )
                val expenseText by animateColorAsState(
                    targetValue = if (isExpenseSelected) Color.White else ExpenseRed,
                    label = "expense_text"
                )

                Surface(
                    onClick = { selectedType = TransactionType.EXPENSE },
                    shape = RoundedCornerShape(16.dp),
                    color = expenseBg,
                    border = BorderStroke(
                        width = if (isExpenseSelected) 2.dp else 1.dp,
                        color = ExpenseRed.copy(alpha = if (isExpenseSelected) 1f else 0.3f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("type_expense_toggle")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            tint = expenseText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "منصرف",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = expenseText
                        )
                    }
                }
            }

            // Person Selector (حساب الشخص)
            if (persons.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = isPersonDropdownExpanded,
                    onExpandedChange = { isPersonDropdownExpanded = !isPersonDropdownExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    OutlinedTextField(
                        value = selectedPerson?.name ?: "اختر حساب الشخص",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("حساب الشخص / العميل *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPersonDropdownExpanded) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = personError,
                        supportingText = if (personError) {
                            { Text("يرجى اختيار الحساب") }
                        } else null,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("select_person_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = isPersonDropdownExpanded,
                        onDismissRequest = { isPersonDropdownExpanded = false }
                    ) {
                        persons.forEach { person ->
                            DropdownMenuItem(
                                text = { Text(person.name, fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    selectedPerson = person
                                    isPersonDropdownExpanded = false
                                    personError = false
                                }
                            )
                        }
                    }
                }
            } else {
                // No persons exist yet -> prompt to create one
                Surface(
                    onClick = onAddNewPersonRequest,
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .testTag("prompt_add_person_sheet")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "اضغط هنا لإضافة أول حساب شخص",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Statement / البيان (Required)
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (titleError && it.isNotBlank()) titleError = false
                },
                label = { Text("البيان (مثلاً: دفعة نقدية / شراء معدات)") },
                trailingIcon = {
                    VoiceInputButton(
                        onSpeechResult = { spoken ->
                            title = spoken
                            titleError = false
                        },
                        tint = MaterialTheme.colorScheme.primary,
                        testTag = "voice_input_sheet_title"
                    )
                },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("يرجى إدخال بيان العملية") }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .testTag("tx_title_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount / المبلغ (Required)
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '.' }) {
                        amountText = input
                        if (amountError && (input.toDoubleOrNull() ?: 0.0) > 0) amountError = false
                    }
                },
                label = { Text("المبلغ") },
                trailingIcon = {
                    VoiceInputButton(
                        onSpeechResult = { spoken ->
                            val digitsOnly = spoken.filter { it.isDigit() || it == '.' }
                            if (digitsOnly.isNotBlank()) {
                                amountText = digitsOnly
                                amountError = false
                            }
                        },
                        tint = MaterialTheme.colorScheme.primary,
                        testTag = "voice_input_sheet_amount"
                    )
                },
                suffix = {
                    Text(
                        text = currency.symbolArabic,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                isError = amountError,
                supportingText = if (amountError) {
                    { Text("يرجى إدخال مبلغ صحيح أكبر من 0") }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_amount_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notes / ملاحظة (Optional)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("ملاحظة (اختياري)") },
                trailingIcon = {
                    VoiceInputButton(
                        onSpeechResult = { spoken ->
                            note = if (note.isBlank()) spoken else "$note $spoken"
                        },
                        tint = MaterialTheme.colorScheme.primary,
                        testTag = "voice_input_sheet_note"
                    )
                },
                maxLines = 2,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_note_input")
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Action Buttons: Save & Cancel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("cancel_tx_button")
                ) {
                    Text("إلغاء", style = MaterialTheme.typography.titleMedium)
                }

                Button(
                    onClick = {
                        val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                        var hasError = false

                        if (selectedPerson == null) {
                            personError = true
                            hasError = true
                        }
                        if (title.isBlank()) {
                            titleError = true
                            hasError = true
                        }
                        if (parsedAmount <= 0) {
                            amountError = true
                            hasError = true
                        }

                        if (!hasError && selectedPerson != null) {
                            onSave(
                                selectedPerson!!.id,
                                selectedPerson!!.name,
                                title.trim(),
                                parsedAmount,
                                selectedType,
                                note.trim()
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == TransactionType.INCOME) IncomeGreen else ExpenseRed
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(50.dp)
                        .testTag("save_tx_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "حفظ العملية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
