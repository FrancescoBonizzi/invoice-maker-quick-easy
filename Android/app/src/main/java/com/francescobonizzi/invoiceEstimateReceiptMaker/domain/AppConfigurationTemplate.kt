package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class AppConfigurationTemplate(
    var layoutId: Int? = null,
    var colorId: Int? = null,
    var primaryColor: String? = null,
    var primaryColorLighter: String? = null,
    var textColorOverPrimaryColor: String? = null,
    var keyValueLabelColor: String? = null,
    var commonTextColor: String? = null
)