package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import org.koin.android.ext.android.inject

class InvoiceDetailActivity : AppCompatActivity()
{
    private val _logger: ILogger by inject()

    private lateinit var _navigationHost: NavController

    private var currentInvoiceEditor: InvoiceEditor? = null

    fun getCurrentInvoiceEditor() : InvoiceEditor
    {
        if (currentInvoiceEditor == null)
        {
            try
            {
                // Argument from the caller
                val invoiceId = intent.extras?.getInt("invoiceId")
                currentInvoiceEditor = if (invoiceId != null)
                {
                    InvoiceEditor(invoiceId)
                }
                else
                {
                    InvoiceEditor()
                }
            }
            catch (ex: Exception)
            {
                _logger.error(ex)
                DialogsHelpers.showError(this, getString(R.string.generic_error))
                finish()
            }

        }

        return currentInvoiceEditor!!
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_invoice_detail)

        // Nascondo la support bar perché è brutta
        supportActionBar?.hide()
        _navigationHost = findNavController(R.id.navigation_host)
    }

}