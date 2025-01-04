package com.francescobonizzi.invoiceEstimateReceiptMaker.storage

import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor

/** Lo utilizzo per passare oggetti complessi tra i fragment */
class InMemoryInvoicePreviewBundler
{
    private var _invoiceEditor: InvoiceEditor? = null

    fun set(invoiceEditor: InvoiceEditor)
    {
        _invoiceEditor = invoiceEditor
    }

    fun get(): InvoiceEditor
    {
        return _invoiceEditor!!
    }
}