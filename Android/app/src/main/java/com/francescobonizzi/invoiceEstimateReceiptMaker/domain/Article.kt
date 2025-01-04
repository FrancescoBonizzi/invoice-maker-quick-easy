package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class Article(
    var name: String? = null,
    var singleItemPrice: Double = 0.0,
    var description: String? = null
)