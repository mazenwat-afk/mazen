package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PersonEntity
import com.example.data.local.PreferencesDataStore
import com.example.data.local.ThemeMode
import com.example.data.local.TransactionEntity
import com.example.data.local.UserSettings
import com.example.data.model.AnimationEffectStyle
import com.example.data.model.AppCurrency
import com.example.data.model.AppThemeStyle
import com.example.data.model.DateFilterPeriod
import com.example.data.model.PersonSummary
import com.example.data.model.TransactionSort
import com.example.data.model.TransactionType
import com.example.data.repository.LedgerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val preferences = PreferencesDataStore(application)
    private val repository = LedgerRepository(database.transactionDao(), database.personDao(), preferences)

    val userSettings: StateFlow<UserSettings> = repository.userSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserSettings()
    )

    val allPersons: StateFlow<List<PersonEntity>> = repository.allPersons.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val personsWithSummary: StateFlow<List<PersonSummary>> = repository.personsWithSummary.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.recentTransactions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val totalIncome: StateFlow<Double> = repository.totalIncome.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.0
    )

    val totalExpense: StateFlow<Double> = repository.totalExpense.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.0
    )

    val currentBalance: StateFlow<Double> = repository.currentBalance.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.0
    )

    // Active/Selected Person for detail view
    private val _selectedPersonId = MutableStateFlow<Long?>(null)
    val selectedPersonId: StateFlow<Long?> = _selectedPersonId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPersonTransactions: StateFlow<List<TransactionEntity>> = _selectedPersonId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getTransactionsForPerson(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPersonSummary: StateFlow<PersonSummary?> = combine(
        personsWithSummary,
        _selectedPersonId
    ) { summaries, id ->
        if (id == null) null else summaries.firstOrNull { it.person.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filter and Search states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow("ALL") // "ALL", "INCOME", "EXPENSE"
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _datePeriod = MutableStateFlow(DateFilterPeriod.ALL)
    val datePeriod: StateFlow<DateFilterPeriod> = _datePeriod.asStateFlow()

    private val _sortOrder = MutableStateFlow(TransactionSort.NEWEST)
    val sortOrder: StateFlow<TransactionSort> = _sortOrder.asStateFlow()

    // Add Transaction Sheet state
    private val _isAddSheetOpen = MutableStateFlow(false)
    val isAddSheetOpen: StateFlow<Boolean> = _isAddSheetOpen.asStateFlow()

    private val _addSheetType = MutableStateFlow(TransactionType.INCOME)
    val addSheetType: StateFlow<TransactionType> = _addSheetType.asStateFlow()

    private val _targetPersonId = MutableStateFlow<Long?>(null)
    val targetPersonId: StateFlow<Long?> = _targetPersonId.asStateFlow()

    // Add/Edit Person Dialog state
    private val _isAddPersonDialogOpen = MutableStateFlow(false)
    val isAddPersonDialogOpen: StateFlow<Boolean> = _isAddPersonDialogOpen.asStateFlow()

    // Deletion states
    private val _transactionToDelete = MutableStateFlow<TransactionEntity?>(null)
    val transactionToDelete: StateFlow<TransactionEntity?> = _transactionToDelete.asStateFlow()

    private val _personToDelete = MutableStateFlow<PersonEntity?>(null)
    val personToDelete: StateFlow<PersonEntity?> = _personToDelete.asStateFlow()

    private val _personToEdit = MutableStateFlow<PersonEntity?>(null)
    val personToEdit: StateFlow<PersonEntity?> = _personToEdit.asStateFlow()

    // Filtered persons list based on search query
    val filteredPersonsWithSummary: StateFlow<List<PersonSummary>> = combine(
        personsWithSummary,
        _searchQuery
    ) { summaries, query ->
        if (query.isBlank()) {
            summaries
        } else {
            summaries.filter {
                it.person.name.contains(query, ignoreCase = true) ||
                        it.person.phone.contains(query, ignoreCase = true) ||
                        it.person.note.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered transactions for the movement/history screen
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _searchQuery,
        _filterType,
        _datePeriod,
        _sortOrder
    ) { transactions, query, typeFilter, period, sort ->
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        val filtered = transactions.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.personName.contains(query, ignoreCase = true) ||
                    item.note.contains(query, ignoreCase = true)

            val matchesType = when (typeFilter) {
                "INCOME" -> item.transactionType == TransactionType.INCOME
                "EXPENSE" -> item.transactionType == TransactionType.EXPENSE
                else -> true
            }

            val matchesPeriod = when (period) {
                DateFilterPeriod.ALL -> true
                DateFilterPeriod.TODAY -> {
                    calendar.timeInMillis = now
                    val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
                    val currentYear = calendar.get(Calendar.YEAR)
                    calendar.timeInMillis = item.timestamp
                    calendar.get(Calendar.DAY_OF_YEAR) == currentDay && calendar.get(Calendar.YEAR) == currentYear
                }
                DateFilterPeriod.THIS_WEEK -> {
                    calendar.timeInMillis = now
                    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
                    val currentYear = calendar.get(Calendar.YEAR)
                    calendar.timeInMillis = item.timestamp
                    calendar.get(Calendar.WEEK_OF_YEAR) == currentWeek && calendar.get(Calendar.YEAR) == currentYear
                }
                DateFilterPeriod.THIS_MONTH -> {
                    calendar.timeInMillis = now
                    val currentMonth = calendar.get(Calendar.MONTH)
                    val currentYear = calendar.get(Calendar.YEAR)
                    calendar.timeInMillis = item.timestamp
                    calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear
                }
            }

            matchesQuery && matchesType && matchesPeriod
        }

        when (sort) {
            TransactionSort.NEWEST -> filtered.sortedByDescending { it.timestamp }
            TransactionSort.OLDEST -> filtered.sortedBy { it.timestamp }
            TransactionSort.HIGHEST_AMOUNT -> filtered.sortedByDescending { it.amount }
            TransactionSort.LOWEST_AMOUNT -> filtered.sortedBy { it.amount }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun selectPerson(personId: Long?) {
        _selectedPersonId.value = personId
    }

    fun openAddPersonDialog() {
        _isAddPersonDialogOpen.value = true
    }

    fun closeAddPersonDialog() {
        _isAddPersonDialogOpen.value = false
    }

    fun createPerson(name: String, phone: String = "", note: String = "", onCreated: (Long) -> Unit = {}) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val entity = PersonEntity(
                name = name.trim(),
                phone = phone.trim(),
                note = note.trim()
            )
            val newId = repository.insertPerson(entity)
            closeAddPersonDialog()
            onCreated(newId)
        }
    }

    fun confirmDeletePerson(person: PersonEntity?) {
        _personToDelete.value = person
    }

    fun openEditPersonDialog(person: PersonEntity) {
        _personToEdit.value = person
    }

    fun closeEditPersonDialog() {
        _personToEdit.value = null
    }

    fun updatePersonName(personId: Long, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.updatePersonName(personId, newName.trim())
            closeEditPersonDialog()
        }
    }

    fun deletePersonConfirmed() {
        val person = _personToDelete.value ?: return
        viewModelScope.launch {
            repository.deletePerson(person.id)
            if (_selectedPersonId.value == person.id) {
                _selectedPersonId.value = null
            }
            _personToDelete.value = null
        }
    }

    fun openAddSheet(
        type: TransactionType = TransactionType.INCOME,
        preselectedPersonId: Long? = null
    ) {
        _addSheetType.value = type
        _targetPersonId.value = preselectedPersonId ?: _selectedPersonId.value
        _isAddSheetOpen.value = true
    }

    fun closeAddSheet() {
        _isAddSheetOpen.value = false
        _targetPersonId.value = null
    }

    fun addTransaction(
        personId: Long,
        personName: String,
        title: String,
        amount: Double,
        type: TransactionType,
        note: String = ""
    ) {
        if (title.isBlank() || amount <= 0) return
        viewModelScope.launch {
            val entity = TransactionEntity(
                personId = personId,
                personName = personName.trim(),
                title = title.trim(),
                amount = amount,
                type = type.name,
                category = "عام",
                note = note.trim(),
                timestamp = System.currentTimeMillis()
            )
            repository.insertTransaction(entity)
            closeAddSheet()
        }
    }

    fun confirmDeleteTransaction(transaction: TransactionEntity?) {
        _transactionToDelete.value = transaction
    }

    fun deleteTransactionConfirmed() {
        val transaction = _transactionToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _transactionToDelete.value = null
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedPersonId.value = null
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun setDatePeriod(period: DateFilterPeriod) {
        _datePeriod.value = period
    }

    fun setSortOrder(sort: TransactionSort) {
        _sortOrder.value = sort
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun setThemeStyle(style: AppThemeStyle) {
        viewModelScope.launch {
            repository.setThemeStyle(style)
        }
    }

    fun setCurrency(currency: AppCurrency) {
        viewModelScope.launch {
            repository.setCurrency(currency)
        }
    }

    fun setAnimationStyle(style: AnimationEffectStyle) {
        viewModelScope.launch {
            repository.setAnimationStyle(style)
        }
    }

    fun setCompactMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setCompactMode(enabled)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setBiometricEnabled(enabled)
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAppLockEnabled(enabled)
        }
    }

    fun setPinCode(pin: String) {
        viewModelScope.launch {
            repository.setPinCode(pin)
        }
    }

    fun importBackupData(
        importedPersons: List<PersonEntity>,
        importedTransactions: List<TransactionEntity>,
        onSuccess: (Int, Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                var pCount = 0
                var tCount = 0
                // We keep track of old person ID to new inserted person ID to preserve relations
                val oldToNewPersonId = mutableMapOf<Long, Long>()
                
                for (person in importedPersons) {
                    val newPerson = PersonEntity(
                        name = person.name,
                        phone = person.phone,
                        note = person.note,
                        createdAt = if (person.createdAt > 0) person.createdAt else System.currentTimeMillis()
                    )
                    val newId = repository.insertPerson(newPerson)
                    oldToNewPersonId[person.id] = newId
                    pCount++
                }

                for (tx in importedTransactions) {
                    val resolvedPersonId = oldToNewPersonId[tx.personId] ?: tx.personId
                    val newTx = TransactionEntity(
                        personId = resolvedPersonId,
                        personName = tx.personName,
                        title = tx.title,
                        amount = tx.amount,
                        type = tx.type,
                        category = tx.category,
                        note = tx.note,
                        timestamp = if (tx.timestamp > 0) tx.timestamp else System.currentTimeMillis()
                    )
                    repository.insertTransaction(newTx)
                    tCount++
                }

                onSuccess(pCount, tCount)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "حدث خطأ أثناء استيراد البيانات")
            }
        }
    }
}
