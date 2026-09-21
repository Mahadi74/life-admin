package com.lifeadmin.app.core.util

import java.text.NumberFormat
import java.util.*

/**
 * Currency formatting utilities
 */
object CurrencyUtils {
    
    /**
     * Supported currencies with symbols
     */
    val SUPPORTED_CURRENCIES = listOf(
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "JPY" to "¥",
        "CNY" to "¥",
        "INR" to "₹",
        "AUD" to "A$",
        "CAD" to "C$",
        "CHF" to "CHF",
        "SEK" to "kr",
        "NZD" to "NZ$",
        "KRW" to "₩",
        "SGD" to "S$",
        "NOK" to "kr",
        "MXN" to "$",
        "BRL" to "R$",
        "ZAR" to "R",
        "RUB" to "₽",
        "AED" to "د.إ"
    )
    
    /**
     * Get currency symbol
     */
    fun getCurrencySymbol(currencyCode: String?): String {
        if (currencyCode == null) return "$"
        return SUPPORTED_CURRENCIES.find { it.first == currencyCode }?.second ?: currencyCode
    }
    
    /**
     * Format amount with currency
     * Examples: "$15.99", "€10.00", "¥1,000"
     */
    fun formatAmount(amount: Double?, currencyCode: String?): String {
        if (amount == null) return ""
        
        val currency = currencyCode ?: "USD"
        
        return try {
            val locale = when (currency) {
                "USD", "CAD", "MXN", "AUD", "NZD", "SGD" -> Locale.US
                "EUR" -> Locale.GERMANY
                "GBP" -> Locale.UK
                "JPY", "CNY", "KRW" -> Locale.JAPAN
                "INR" -> Locale("en", "IN")
                else -> Locale.US
            }
            
            val formatter = NumberFormat.getCurrencyInstance(locale)
            formatter.currency = Currency.getInstance(currency)
            formatter.format(amount)
        } catch (e: Exception) {
            // Fallback to simple formatting
            "${getCurrencySymbol(currency)}%.2f".format(amount)
        }
    }
    
    /**
     * Format amount without currency symbol (for input fields)
     */
    fun formatAmountPlain(amount: Double?): String {
        if (amount == null) return ""
        return "%.2f".format(amount)
    }
    
    /**
     * Parse amount from string
     */
    fun parseAmount(amountStr: String): Double? {
        return try {
            amountStr.replace(Regex("[^0-9.]"), "").toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
