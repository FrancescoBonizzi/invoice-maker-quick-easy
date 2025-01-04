package com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions

import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.AppConfiguration

interface IAppConfigurationRepository
{
    fun get(): AppConfiguration
    fun save()
}