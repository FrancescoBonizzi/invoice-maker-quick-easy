package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

import java.io.Serializable
import java.util.*

data class Invoice(
    var userDefinedId: Int? = null,
    var code: String? = null,

    var customer: InvoiceCustomer? = null,

    var paymentDate: Date? = null,
    var creationDate: Date? = null,
    var expirationDate: Date? = null,

    var company: InvoiceCompany? = null,
    var regionalSettings: AppConfigurationRegionalSettings? = null,
    var articles: MutableList<InvoiceArticle>,
    var template: InvoiceTemplate? = null,
    var payment: InvoicePayment? = null,

    var showInvoiceCode: Boolean = true,
    var showInvoiceCreationDate: Boolean = true,
    var showInvoiceExpirationDate: Boolean = true,
    var showFooterNotes: Boolean = false,
    var footerNotes: String? = null,

    var shouldShowWatermark: Boolean = false
)