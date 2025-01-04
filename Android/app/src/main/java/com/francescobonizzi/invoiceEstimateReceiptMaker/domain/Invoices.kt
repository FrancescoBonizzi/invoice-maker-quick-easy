package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class Invoices(
    var identity: Int? = null,
    var invoices: MutableList<Invoice>
)