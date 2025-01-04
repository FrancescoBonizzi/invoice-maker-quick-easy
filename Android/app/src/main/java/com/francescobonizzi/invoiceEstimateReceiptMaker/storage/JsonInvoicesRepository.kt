package com.francescobonizzi.invoiceEstimateReceiptMaker.storage

import android.content.Context
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.*
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IInvoicesRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.util.*

/** File json matenuto sempre aggiornato in memoria che gestisce le fatture */
class JsonInvoicesRepository(val context: Context) : IInvoicesRepository, KoinComponent
{
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()
    private val _fileName: String = context.filesDir.path.toString() + "/invoices.json"
    private val _invoices: Invoices
    private val _jsonSerializer: Gson = GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").create()
    private val _logger: ILogger by inject()

    // In inizializzazione carico o creo il file
    init
    {
        val file = File(_fileName)

        if (!file.exists())
        {
            _invoices = Invoices(identity = 0, invoices = mutableListOf())
            save()
        }
        else
        {
            val raw = file.readText()

            try
            {
                _invoices = _jsonSerializer.fromJson(raw, Invoices::class.java)
            }
            catch (ex: Exception)
            {
                _logger.error("Errore nel caricamento del file delle fatture. File: $raw", ex)
                throw Exception("Errore nel caricamento del file delle fatture")
            }
        }
    }

    override fun getIdentity(): Int
    {
        return _invoices.identity!!
    }

    private fun incrementIdentity()
    {
        _invoices.identity = _invoices.identity!! + 1
    }

    override fun getAll(): MutableList<Invoice>
    {
        return _invoices.invoices
    }

    override fun get(id: Int): Invoice
    {
        return _invoices.invoices[id]
    }

    override fun add(invoice: Invoice)
    {
        _invoices.invoices.add(0, invoice)
        val appConfiguration = _appConfigurationRepository.get()

        if (appConfiguration.payments.invoiceUserDefinedLastId < invoice.userDefinedId!!)
        {
            appConfiguration.payments.invoiceUserDefinedLastId = invoice.userDefinedId!!
        }

        // Incremento l'identity solo quando effettivamente aggiungo una nuova fattura alla lista
        incrementIdentity()
        _appConfigurationRepository.save()
    }

    override fun save()
    {
        val file = File(_fileName)
        if (!file.exists())
        {
            file.createNewFile()
        }

        val serialized = _jsonSerializer.toJson(_invoices)
        file.writeText(serialized)
    }

    override fun delete(invoice: Invoice)
    {
        _invoices.invoices.remove(invoice)
        save()
    }

    override fun createEmptyInvoice(): Invoice
    {
        val appConfiguration = _appConfigurationRepository.get()
        val userDefinedId = appConfiguration.payments.invoiceUserDefinedLastId + 1
        val invoiceCode = appConfiguration.payments.invoicePrefix + userDefinedId

        val creationDate = Date()
        var expirationDate: Date? = null

        if (appConfiguration.payments.defaultInvoiceExpirationDays != null)
        {
            val calendar = Calendar.getInstance()
            calendar.time = creationDate
            calendar.add(
                Calendar.DAY_OF_YEAR,
                appConfiguration.payments.defaultInvoiceExpirationDays!!)
            expirationDate = calendar.time
        }

        return Invoice(
            code = invoiceCode,
            userDefinedId = userDefinedId,
            creationDate = creationDate,
            expirationDate = expirationDate,
            customer = InvoiceCustomer(
                showCustomerName = true
            ),
            company = InvoiceCompany(
                showCompanyDetails = appConfiguration.company.showCompanyDetails,
                showCompanyLogo = appConfiguration.company.showCompanyLogo,
                showSignature = appConfiguration.company.showSignature,
                name = appConfiguration.company.name,
                holderName = appConfiguration.company.holderName,
                vatNumber = appConfiguration.company.vatNumber,
                address1 = appConfiguration.company.address1,
                phone1 = appConfiguration.company.phone1,
                phone2 = appConfiguration.company.phone2,
                email1 = appConfiguration.company.email1,
                webSite = appConfiguration.company.webSite
            ),

            regionalSettings = appConfiguration.regionalSettings,

            articles = mutableListOf(),

            template = InvoiceTemplate(
                layoutId = appConfiguration.template.layoutId,
                colorId = appConfiguration.template.colorId,
                primaryColor = appConfiguration.template.primaryColor,
                primaryColorLighter = appConfiguration.template.primaryColorLighter,
                textColorOverPrimaryColor = appConfiguration.template.textColorOverPrimaryColor,
                keyValueLabelColor = appConfiguration.template.keyValueLabelColor,
                commonTextColor = appConfiguration.template.commonTextColor
            ),

            payment = InvoicePayment(
                applyTaxes = appConfiguration.payments.isVATEnabled,
                applyDiscount = appConfiguration.payments.applyDiscount,
                showPayPalEmail = appConfiguration.payments.showPayPalEmail,
                showBankTransferDetails = appConfiguration.payments.showBankTransferDetails,
                showPaymentNotes = appConfiguration.payments.showPaymentNotes,
                payPalEmail = appConfiguration.payments.payPalEmail,
                bankDetails = appConfiguration.payments.bankDetails,
                checkHolder = appConfiguration.payments.checkHolder,
                vatPercentage = appConfiguration.payments.vatPercentage,
                vatLabel = appConfiguration.payments.vatLabel,
                paymentNotes = appConfiguration.payments.paymentNotes,
                discountPercentage = appConfiguration.payments.discountPercentage,
                showCash = appConfiguration.payments.showCash,
                showCreditCard = appConfiguration.payments.showCreditCard,
                showDebitCard = appConfiguration.payments.showDebitCard),

            showInvoiceCode = appConfiguration.payments.showInvoiceCode,
            showInvoiceCreationDate = appConfiguration.payments.showInvoiceCreationDate,
            showInvoiceExpirationDate = appConfiguration.payments.showInvoiceExpirationDate,
            footerNotes = appConfiguration.payments.defaultInvoicesNotes,
            showFooterNotes = appConfiguration.payments.showInvoiceNotes
        )
    }

}