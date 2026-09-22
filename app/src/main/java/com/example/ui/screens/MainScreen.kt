package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AnimationEffectStyle
import com.example.data.model.TransactionType
import com.example.ui.components.AddPersonDialog
import com.example.ui.components.AddTransactionSheet
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.EditPersonDialog
import com.example.ui.components.LedgerFormatters
import com.example.ui.viewmodel.LedgerViewModel

enum class NavigationTab(val title: String, val index: Int) {
    DASHBOARD("الحسابات", 0),
    TRANSACTIONS("الحركات", 1),
    SETTINGS("الإعدادات", 2)
}

@Composable
fun MainScreen(
    viewModel: LedgerViewModel,
    modifier: Modifier = Modifier
) {
    // Provide natural RTL Arabic layout direction for the main UI
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
        val allPersons by viewModel.allPersons.collectAsStateWithLifecycle()
        val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
        val filteredPersonsWithSummary by viewModel.filteredPersonsWithSummary.collectAsStateWithLifecycle()
        val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()

        val selectedPersonId by viewModel.selectedPersonId.collectAsStateWithLifecycle()
        val selectedPersonSummary by viewModel.selectedPersonSummary.collectAsStateWithLifecycle()
        val selectedPersonTransactions by viewModel.selectedPersonTransactions.collectAsStateWithLifecycle()

        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val filterType by viewModel.filterType.collectAsStateWithLifecycle()
        val datePeriod by viewModel.datePeriod.collectAsStateWithLifecycle()
        val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

        val isAddSheetOpen by viewModel.isAddSheetOpen.collectAsStateWithLifecycle()
        val addSheetType by viewModel.addSheetType.collectAsStateWithLifecycle()
        val targetPersonId by viewModel.targetPersonId.collectAsStateWithLifecycle()

        val isAddPersonDialogOpen by viewModel.isAddPersonDialogOpen.collectAsStateWithLifecycle()
        val personToEdit by viewModel.personToEdit.collectAsStateWithLifecycle()
        val transactionToDelete by viewModel.transactionToDelete.collectAsStateWithLifecycle()
        val personToDelete by viewModel.personToDelete.collectAsStateWithLifecycle()

        var currentTabIndex by rememberSaveable { mutableIntStateOf(NavigationTab.DASHBOARD.index) }

        // BackHandler: if inside a person detail screen, back returns to dashboard
        BackHandler(enabled = selectedPersonId != null) {
            viewModel.selectPerson(null)
        }

        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp,
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bottom_nav_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tab 1: Accounts (الحسابات)
                            FloatingDockItem(
                                selected = currentTabIndex == NavigationTab.DASHBOARD.index && selectedPersonId == null,
                                icon = if (currentTabIndex == NavigationTab.DASHBOARD.index) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                label = "الحسابات",
                                testTag = "nav_tab_accounts",
                                onClick = {
                                    viewModel.selectPerson(null)
                                    currentTabIndex = NavigationTab.DASHBOARD.index
                                }
                            )

                            // Tab 2: Transactions / Movements (الحركات)
                            FloatingDockItem(
                                selected = currentTabIndex == NavigationTab.TRANSACTIONS.index,
                                icon = if (currentTabIndex == NavigationTab.TRANSACTIONS.index) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                label = "الحركات",
                                testTag = "nav_tab_transactions",
                                onClick = {
                                    viewModel.selectPerson(null)
                                    currentTabIndex = NavigationTab.TRANSACTIONS.index
                                }
                            )

                            // Tab 3: Settings (الإعدادات)
                            FloatingDockItem(
                                selected = currentTabIndex == NavigationTab.SETTINGS.index,
                                icon = if (currentTabIndex == NavigationTab.SETTINGS.index) Icons.Filled.Settings else Icons.Outlined.Settings,
                                label = "الإعدادات",
                                testTag = "nav_tab_settings",
                                onClick = {
                                    viewModel.selectPerson(null)
                                    currentTabIndex = NavigationTab.SETTINGS.index
                                }
                            )
                        }
                    }

                    // Bottom-left credit requirement: "واكتب تحت شمال الشاشه بخط صغير Made by: Mazen"
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 14.dp, top = 4.dp, bottom = 2.dp)
                        ) {
                            Text(
                                text = "Made by: Mazen",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .testTag("made_by_mazen_label")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // If a person is selected, show their specific minimal detail view!
                if (selectedPersonSummary != null) {
                    PersonDetailScreen(
                        summary = selectedPersonSummary!!,
                        transactions = selectedPersonTransactions,
                        userSettings = userSettings,
                        onBack = { viewModel.selectPerson(null) },
                        onAddTransaction = { amount, statement, type ->
                            viewModel.addTransaction(
                                personId = selectedPersonSummary!!.person.id,
                                personName = selectedPersonSummary!!.person.name,
                                title = statement,
                                amount = amount,
                                type = type
                            )
                        },
                        onEditPersonName = { viewModel.openEditPersonDialog(selectedPersonSummary!!.person) },
                        onDeleteTransaction = { tx -> viewModel.confirmDeleteTransaction(tx) },
                        onDeletePerson = { viewModel.confirmDeletePerson(selectedPersonSummary!!.person) }
                    )
                } else {
                    AnimatedContent(
                        targetState = currentTabIndex,
                        transitionSpec = {
                            when (userSettings.animationStyle) {
                                AnimationEffectStyle.BOUNCY -> {
                                    (slideInHorizontally(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { it / 3 } + fadeIn()) togetherWith
                                            (slideOutHorizontally(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { -it / 3 } + fadeOut())
                                }
                                AnimationEffectStyle.SLIDE -> {
                                    if (targetState > initialState) {
                                        (slideInHorizontally(tween(260)) { it } + fadeIn(tween(260))) togetherWith
                                                (slideOutHorizontally(tween(260)) { -it } + fadeOut(tween(260)))
                                    } else {
                                        (slideInHorizontally(tween(260)) { -it } + fadeIn(tween(260))) togetherWith
                                                (slideOutHorizontally(tween(260)) { it } + fadeOut(tween(260)))
                                    }
                                }
                                AnimationEffectStyle.MINIMAL -> {
                                    fadeIn(tween(120)) togetherWith fadeOut(tween(120))
                                }
                                AnimationEffectStyle.OFF -> {
                                    EnterTransition.None togetherWith ExitTransition.None
                                }
                                AnimationEffectStyle.SMOOTH -> {
                                    (fadeIn(tween(250)) + scaleIn(tween(250), initialScale = 0.97f)) togetherWith
                                            (fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.99f))
                                }
                            }
                        },
                        label = "tab_content_anim"
                    ) { targetIndex ->
                        when (targetIndex) {
                            NavigationTab.DASHBOARD.index -> {
                                DashboardScreen(
                                    personsSummaries = filteredPersonsWithSummary,
                                    userSettings = userSettings,
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    onAddPersonClick = { viewModel.openAddPersonDialog() },
                                    onPersonClick = { person -> viewModel.selectPerson(person.id) },
                                    onEditPersonName = { person -> viewModel.openEditPersonDialog(person) },
                                    onDeletePerson = { person -> viewModel.confirmDeletePerson(person) }
                                )
                            }
                            NavigationTab.TRANSACTIONS.index -> {
                                TransactionsScreen(
                                    transactions = filteredTransactions,
                                    userSettings = userSettings,
                                    searchQuery = searchQuery,
                                    filterType = filterType,
                                    datePeriod = datePeriod,
                                    sortOrder = sortOrder,
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    onFilterTypeChange = { viewModel.setFilterType(it) },
                                    onDatePeriodChange = { viewModel.setDatePeriod(it) },
                                    onSortOrderChange = { viewModel.setSortOrder(it) },
                                    onDeleteTransaction = { tx -> viewModel.confirmDeleteTransaction(tx) }
                                )
                            }
                            NavigationTab.SETTINGS.index -> {
                                SettingsScreen(
                                    userSettings = userSettings,
                                    allPersons = allPersons,
                                    allTransactions = allTransactions,
                                    onThemeModeChange = { viewModel.setThemeMode(it) },
                                    onDarkModeChange = { viewModel.setDarkMode(it) },
                                    onThemeStyleChange = { viewModel.setThemeStyle(it) },
                                    onCurrencyChange = { viewModel.setCurrency(it) },
                                    onAnimationStyleChange = { viewModel.setAnimationStyle(it) },
                                    onCompactModeChange = { viewModel.setCompactMode(it) },
                                    onImportData = { persons, txs, onSuccess, onError ->
                                        viewModel.importBackupData(persons, txs, onSuccess, onError)
                                    },
                                    onClearAllData = { viewModel.clearAllData() }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Transaction Modal Bottom Sheet (from general transactions screen if used)
        if (isAddSheetOpen) {
            AddTransactionSheet(
                initialType = addSheetType,
                currency = userSettings.currency,
                persons = allPersons,
                preselectedPersonId = targetPersonId,
                onDismiss = { viewModel.closeAddSheet() },
                onSave = { personId, personName, title, amount, type, note ->
                    viewModel.addTransaction(personId, personName, title, amount, type, note)
                },
                onAddNewPersonRequest = {
                    viewModel.closeAddSheet()
                    viewModel.openAddPersonDialog()
                }
            )
        }

        // Add Person Dialog (Minimal: Person Name + Save button only!)
        if (isAddPersonDialogOpen) {
            AddPersonDialog(
                onDismiss = { viewModel.closeAddPersonDialog() },
                onConfirm = { name ->
                    viewModel.createPerson(name)
                }
            )
        }

        // Edit Person Name Dialog (Minimal: Current Name + Save button!)
        personToEdit?.let { person ->
            EditPersonDialog(
                initialName = person.name,
                onDismiss = { viewModel.closeEditPersonDialog() },
                onConfirm = { newName ->
                    viewModel.updatePersonName(person.id, newName)
                }
            )
        }

        // Delete Single Transaction Confirmation
        transactionToDelete?.let { tx ->
            val isIncome = tx.transactionType == TransactionType.INCOME
            DeleteConfirmationDialog(
                title = "حذف المعاملة",
                message = "هل تريد حذف العملية \"${tx.title}\" بقيمة ${LedgerFormatters.formatTransactionAmount(tx.amount, isIncome, userSettings.currency)}؟",
                confirmButtonText = "حذف",
                onConfirm = { viewModel.deleteTransactionConfirmed() },
                onDismiss = { viewModel.confirmDeleteTransaction(null) }
            )
        }

        // Delete Person Account Confirmation
        personToDelete?.let { person ->
            DeleteConfirmationDialog(
                title = "حذف حساب ${person.name}",
                message = "سيؤدي هذا الإجراء إلى حذف حساب \"${person.name}\" وجميع العمليات المالية المرتبطة به نهائياً.",
                confirmButtonText = "حذف الحساب",
                onConfirm = { viewModel.deletePersonConfirmed() },
                onDismiss = { viewModel.confirmDeletePerson(null) }
            )
        }
    }
}

@Composable
private fun FloatingDockItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "dock_pill_color"
    )
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = animatedColor,
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            AnimatedVisibility(visible = selected) {
                Row {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = contentColor
                    )
                }
            }
        }
    }
}
