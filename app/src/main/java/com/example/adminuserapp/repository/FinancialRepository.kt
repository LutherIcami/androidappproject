package com.example.adminuserapp.repository

import com.example.adminuserapp.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class FinancialRepository {
    // In a real app, this would be connected to a database or API
    private val invoices = mutableListOf<Invoice>()
    private val expenses = mutableListOf<Expense>()
    private val payments = mutableListOf<Payment>()

    // Invoice operations
    fun getInvoices(): Flow<List<Invoice>> = flow {
        emit(invoices)
    }

    fun getInvoiceById(id: String): Flow<Invoice?> = flow {
        emit(invoices.find { it.id == id })
    }

    fun getInvoicesByStatus(status: InvoiceStatus): Flow<List<Invoice>> = flow {
        emit(invoices.filter { it.status == status })
    }

    fun getInvoicesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Invoice>> = flow {
        emit(invoices.filter { it.issueDate >= startDate && it.issueDate <= endDate })
    }

    suspend fun createInvoice(invoice: Invoice) {
        invoices.add(invoice)
    }

    suspend fun updateInvoice(invoice: Invoice) {
        val index = invoices.indexOfFirst { it.id == invoice.id }
        if (index != -1) {
            invoices[index] = invoice
        }
    }

    // Expense operations
    fun getExpenses(): Flow<List<Expense>> = flow {
        emit(expenses)
    }

    fun getExpenseById(id: String): Flow<Expense?> = flow {
        emit(expenses.find { it.id == id })
    }

    fun getExpensesByStatus(status: ExpenseStatus): Flow<List<Expense>> = flow {
        emit(expenses.filter { it.status == status })
    }

    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>> = flow {
        emit(expenses.filter { it.category == category })
    }

    suspend fun createExpense(expense: Expense) {
        expenses.add(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        val index = expenses.indexOfFirst { it.id == expense.id }
        if (index != -1) {
            expenses[index] = expense
        }
    }

    // Payment operations
    fun getPayments(): Flow<List<Payment>> = flow {
        emit(payments)
    }

    fun getPaymentById(id: String): Flow<Payment?> = flow {
        emit(payments.find { it.id == id })
    }

    fun getPaymentsByStatus(status: PaymentStatus): Flow<List<Payment>> = flow {
        emit(payments.filter { it.status == status })
    }

    suspend fun createPayment(payment: Payment) {
        payments.add(payment)
    }

    suspend fun updatePayment(payment: Payment) {
        val index = payments.indexOfFirst { it.id == payment.id }
        if (index != -1) {
            payments[index] = payment
        }
    }

    // Financial statistics
    fun getFinancialStatistics(): Flow<FinancialStatistics> = flow {
        val totalRevenue = invoices.sumOf { it.amount }
        val totalExpenses = expenses.sumOf { it.amount }
        val totalPayments = payments.sumOf { it.amount }
        val pendingInvoices = invoices.count { it.status == InvoiceStatus.SENT }
        val overdueInvoices = invoices.count { it.status == InvoiceStatus.OVERDUE }
        
        emit(FinancialStatistics(
            totalRevenue = totalRevenue,
            totalExpenses = totalExpenses,
            totalPayments = totalPayments,
            pendingInvoices = pendingInvoices,
            overdueInvoices = overdueInvoices,
            profit = totalRevenue - totalExpenses
        ))
    }
} 