package com.francescobonizzi.invoiceEstimateReceiptMaker.logging

import android.content.Context
import android.os.Bundle

interface ILogger
{
    fun error(message: String)
    fun error(exception: Exception)
    fun error(message: String, exception: Exception)
    fun event(context: Context, eventName: String, eventProperties: Bundle)
}