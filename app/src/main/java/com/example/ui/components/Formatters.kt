package com.example.ui.components

import com.example.data.model.AppCurrency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LedgerFormatters {

    private val numberFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale.US)
        DecimalFormat("#,##0.##", symbols)
    }

    fun formatAmount(amount: Double): String {
        return numberFormatter.format(amount)
    }

    fun formatWithCurrency(amount: Double, currency: AppCurrency): String {
        val formatted = formatAmount(amount)
        return "$formatted ${currency.symbolArabic}"
    }

    fun formatTransactionAmount(amount: Double, isIncome: Boolean, currency: AppCurrency): String {
        val sign = if (isIncome) "+" else "-"
        val formatted = formatAmount(amount)
        return "$sign$formatted ${currency.symbolArabic}"
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.US)
        return sdf.format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return sdf.format(Date(timestamp))
    }
}
