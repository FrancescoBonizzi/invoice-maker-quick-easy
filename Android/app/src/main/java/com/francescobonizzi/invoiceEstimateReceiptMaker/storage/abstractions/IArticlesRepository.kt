package com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions

import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Article

interface IArticlesRepository
{
    fun getAll(): MutableList<Article>
    fun get(articleId: Int): Article
    fun add(article: Article)
    fun delete(article: Article)
    fun save()
}