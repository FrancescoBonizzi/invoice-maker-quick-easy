package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class Customer(
    var name: String? = null,
    var address: String? = null,
    var email: String? = null,
    var phone: String? = null
)