package com.example.adminuserapp.data.models

import java.time.LocalDate
import java.util.UUID

data class Invoice(
    val id: String = UUID.randomUUID().toString(),
    val number: String,
    val customerId: String,
    val projectId: String,
    val amount: Double,
    val status: InvoiceStatus,
    val dueDate: LocalDate,
    val issueDate: LocalDate = LocalDate.now(),
    val items: List<InvoiceItem>,
    val notes: String = "",
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDate = LocalDate.now()
)

data class InvoiceItem(
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val amount: Double
)

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val userId: String,
    val amount: Double,
    val category: ExpenseCategory,
    val description: String,
    val date: LocalDate,
    val status: ExpenseStatus,
    val receiptUrl: String? = null,
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDate = LocalDate.now()
)

data class Payment(
    val id: String = UUID.randomUUID().toString(),
    val invoiceId: String,
    val amount: Double,
    val date: LocalDate,
    val method: PaymentMethod,
    val status: PaymentStatus,
    val reference: String,
    val notes: String = "",
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDate = LocalDate.now()
)

enum class InvoiceStatus {
    DRAFT,
    SENT,
    PAID,
    OVERDUE,
    CANCELLED
}

enum class ExpenseStatus {
    PENDING,
    APPROVED,
    REJECTED,
    PAID
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}

enum class PaymentMethod {
    BANK_TRANSFER,
    CREDIT_CARD,
    CASH,
    CHECK,
    OTHER
}

enum class ExpenseCategory {
    TRAVEL,
    MEALS,
    SUPPLIES,
    EQUIPMENT,
    SOFTWARE,
    SERVICES,
    OTHER
} 