package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class TemplateColor(
    val id: Int,
    var primaryColor: String,
    var primaryColorLighter: String,
    var textColorOverPrimaryColor: String,
    var keyValueLabelColor: String,
    var commonTextColor: String)
