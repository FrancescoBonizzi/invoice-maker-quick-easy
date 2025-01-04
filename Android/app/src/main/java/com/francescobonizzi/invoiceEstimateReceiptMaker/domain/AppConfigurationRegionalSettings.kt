package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class AppConfigurationRegionalSettings(
    var iso4217currencyCode: String? = null,
    var dateFormat: String? = null
)