package com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions

import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Customer

interface ICustomersRepository
{
    fun getAll(): MutableList<Customer>
    fun get(customerId: Int): Customer
    fun add(customer: Customer)
    fun delete(customer: Customer)
    fun save()
}