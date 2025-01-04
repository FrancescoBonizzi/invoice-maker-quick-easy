package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

data class InvoiceArticle(
    var name: String? = null,
    var quantity: Int = 0,
    var description: String? = null,
    var singleItemPrice: Double = 0.0,
    var formattedSingleItemPrice: String? = null,
    var formattedFinalAmount: String? = null
)
{
    constructor(article: Article) : this(
        article.name,
        quantity = 1,
        description = article.description,
        singleItemPrice = article.singleItemPrice,
        formattedSingleItemPrice = null,
        formattedFinalAmount = null)
}