package com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions

import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice

interface IInvoicesRepository
{
    fun getAll(): MutableList<Invoice>
    fun get(id: Int): Invoice
    fun createEmptyInvoice(): Invoice
    fun add(invoice: Invoice)
    fun save()
    fun delete(invoice: Invoice)
    fun getIdentity(): Int
}