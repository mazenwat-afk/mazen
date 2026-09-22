package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personId: Long = 0,
    val personName: String = "",
    val title: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String = "عام",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val transactionType: TransactionType
        get() = if (type.equals("EXPENSE", ignoreCase = true)) {
            TransactionType.EXPENSE
        } else {
            TransactionType.INCOME
        }
}
