package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.PersonDao
import com.example.data.local.PersonEntity
import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import com.example.data.model.AnimationEffectStyle
import com.example.data.model.AppCurrency
import com.example.data.model.TransactionType
import com.example.ui.components.LedgerFormatters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MazenLedgerLogicTest {

    private lateinit var database: AppDatabase
    private lateinit var transactionDao: TransactionDao
    private lateinit var personDao: PersonDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        transactionDao = database.transactionDao()
        personDao = database.personDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testPersonCreationAndTransactions() = runBlocking {
        // Create Person account
        val personId = personDao.insertPerson(
            PersonEntity(name = "أحمد محمد", phone = "0912345678")
        )
        assertNotNull(personId)

        // Insert Income for Person: 50,000
        transactionDao.insertTransaction(
            TransactionEntity(
                personId = personId,
                personName = "أحمد محمد",
                title = "دفعة كاش",
                amount = 50000.0,
                type = TransactionType.INCOME.name
            )
        )

        // Insert Expense for Person: 20,000
        transactionDao.insertTransaction(
            TransactionEntity(
                personId = personId,
                personName = "أحمد محمد",
                title = "شراء مواد",
                amount = 20000.0,
                type = TransactionType.EXPENSE.name
            )
        )

        val personIncome = transactionDao.getPersonIncome(personId).first() ?: 0.0
        val personExpense = transactionDao.getPersonExpense(personId).first() ?: 0.0
        val netBalance = personIncome - personExpense

        assertEquals(50000.0, personIncome, 0.01)
        assertEquals(20000.0, personExpense, 0.01)
        assertEquals(30000.0, netBalance, 0.01)

        val personTxs = transactionDao.getTransactionsForPerson(personId).first()
        assertEquals(2, personTxs.size)
    }

    @Test
    fun testInsertAndCalculateTotalBalance() = runBlocking {
        transactionDao.insertTransaction(
            TransactionEntity(
                title = "بيع بضاعة",
                amount = 1730.0,
                type = TransactionType.INCOME.name
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                title = "شراء مستلزمات",
                amount = 1170.0,
                type = TransactionType.EXPENSE.name
            )
        )

        val income = transactionDao.getTotalIncome().first() ?: 0.0
        val expense = transactionDao.getTotalExpense().first() ?: 0.0
        val balance = income - expense

        assertEquals(1730.0, income, 0.01)
        assertEquals(1170.0, expense, 0.01)
        assertEquals(560.0, balance, 0.01)
    }

    @Test
    fun testCurrencyFormatting() {
        val formattedSDG = LedgerFormatters.formatWithCurrency(560.0, AppCurrency.SDG)
        assertEquals("560 جنيه", formattedSDG)

        val formattedUSD = LedgerFormatters.formatWithCurrency(1200.0, AppCurrency.USD)
        assertEquals("1,200 $", formattedUSD)
    }

    @Test
    fun testAnimationStylesExist() {
        val styles = AnimationEffectStyle.entries
        assertEquals(5, styles.size)
    }
}
