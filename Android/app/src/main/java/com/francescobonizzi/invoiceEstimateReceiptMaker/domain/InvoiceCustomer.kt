package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class InvoiceCustomer(
    var showCustomerName: Boolean = false,
    var name: String? = null,
    var address: String? = null,
    var email: String? = null,
    var phone: String? = null
)