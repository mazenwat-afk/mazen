package com.example.data.repository

import com.example.data.local.PersonDao
import com.example.data.local.PersonEntity
import com.example.data.local.PreferencesDataStore
import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import com.example.data.local.ThemeMode
import com.example.data.local.UserSettings
import com.example.data.model.AnimationEffectStyle
import com.example.data.model.AppCurrency
import com.example.data.model.AppThemeStyle
import com.example.data.model.PersonSummary
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class LedgerRepository(
    private val transactionDao: TransactionDao,
    private val personDao: PersonDao,
    private val preferencesDataStore: PreferencesDataStore
) {

    val allPersons: Flow<List<PersonEntity>> = personDao.getAllPersons()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    val recentTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions(8)

    val totalIncome: Flow<Double> = transactionDao.getTotalIncome().mapZeroIfNull()

    val totalExpense: Flow<Double> = transactionDao.getTotalExpense().mapZeroIfNull()

    val currentBalance: Flow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }

    val personsWithSummary: Flow<List<PersonSummary>> =
        combine(allPersons, allTransactions) { persons, transactions ->
            persons.map { person ->
                val personTxs = transactions.filter { it.personId == person.id }
                val income = personTxs.filter { it.transactionType == TransactionType.INCOME }
                    .sumOf { it.amount }
                val expense = personTxs.filter { it.transactionType == TransactionType.EXPENSE }
                    .sumOf { it.amount }
                val lastTime = personTxs.maxOfOrNull { it.timestamp } ?: person.createdAt

                PersonSummary(
                    person = person,
                    totalIncome = income,
                    totalExpense = expense,
                    netBalance = income - expense,
                    transactionCount = personTxs.size,
                    lastTimestamp = lastTime
                )
            }
        }

    val userSettings: Flow<UserSettings> = preferencesDataStore.userSettingsFlow

    fun getTransactionsForPerson(personId: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsForPerson(personId)
    }

    suspend fun insertPerson(person: PersonEntity): Long {
        return personDao.insertPerson(person)
    }

    suspend fun updatePerson(person: PersonEntity) {
        personDao.updatePerson(person)
    }

    suspend fun updatePersonName(personId: Long, newName: String) {
        personDao.updatePersonName(personId, newName)
        transactionDao.updatePersonNameInTransactions(personId, newName)
    }

    suspend fun deletePerson(personId: Long) {
        transactionDao.deleteTransactionsForPerson(personId)
        personDao.deletePersonById(personId)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun clearAllTransactions() {
        transactionDao.clearAll()
    }

    suspend fun clearAllData() {
        transactionDao.clearAll()
        personDao.clearAllPersons()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        preferencesDataStore.setThemeMode(mode)
    }

    suspend fun setDarkMode(enabled: Boolean) {
        preferencesDataStore.setDarkMode(enabled)
    }

    suspend fun setThemeStyle(themeStyle: AppThemeStyle) {
        preferencesDataStore.setThemeStyle(themeStyle)
    }

    suspend fun setCurrency(currency: AppCurrency) {
        preferencesDataStore.setCurrency(currency)
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        preferencesDataStore.setAnimationsEnabled(enabled)
    }

    suspend fun setAnimationStyle(style: AnimationEffectStyle) {
        preferencesDataStore.setAnimationStyle(style)
    }

    suspend fun setCompactMode(enabled: Boolean) {
        preferencesDataStore.setCompactMode(enabled)
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        preferencesDataStore.setBiometricEnabled(enabled)
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        preferencesDataStore.setAppLockEnabled(enabled)
    }

    suspend fun setPinCode(pin: String) {
        preferencesDataStore.setPinCode(pin)
    }
}

private fun Flow<Double?>.mapZeroIfNull(): Flow<Double> = this.map { it ?: 0.0 }
