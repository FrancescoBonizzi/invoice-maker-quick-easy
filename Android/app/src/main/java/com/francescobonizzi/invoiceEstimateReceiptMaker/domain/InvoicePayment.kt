package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class InvoicePayment(
    var applyTaxes: Boolean = false,
    var applyDiscount: Boolean = false,
    var showPayPalEmail: Boolean = false,
    var showBankTransferDetails: Boolean = false,
    var showPaymentNotes: Boolean = false,

    var payPalEmail: String? = null,
    var bankDetails: String? = null,
    var checkHolder: String? = null,
    var paymentNotes: String? = null,
    var vatPercentage: Double? = null,
    var vatLabel: String? = null,
    var discountPercentage: Double? = null,

    var showCash: Boolean = false,
    var showCreditCard: Boolean = false,
    var showDebitCard: Boolean = false
)