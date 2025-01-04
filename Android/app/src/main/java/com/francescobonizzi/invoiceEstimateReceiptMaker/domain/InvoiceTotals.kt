package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class InvoiceTotals(
    var formattedPartialAmount: String? = null,
    var formattedDiscountAmount: String? = null,
    var formattedTaxesAmount: String? = null,
    var formattedTotalAmount: String? = null
)