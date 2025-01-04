package com.francescobonizzi.invoiceEstimateReceiptMaker.logic

import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.InvoiceTotals
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IInvoicesRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.NumberFormat

/** Gestore della "sessione" di edit o creazione di un'invoice */
class InvoiceEditor : KoinComponent
{
    val invoice: Invoice

    private val _invoicesRepository: IInvoicesRepository by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _currencyFormat: NumberFormat

    var isNewInvoice: Boolean

    constructor(invoiceId: Int)
    {
        invoice = _invoicesRepository.get(invoiceId)
        isNewInvoice = false
    }

    constructor()
    {
        invoice = _invoicesRepository.createEmptyInvoice()
        isNewInvoice = true
    }

    constructor(alreadyLoadedInvoice: Invoice)
    {
        invoice = alreadyLoadedInvoice
        isNewInvoice = false
    }

    init
    {
        val appConfiguration = _appConfigurationRepository.get()
        _currencyFormat = Helpers.getCurrencyFormat(appConfiguration)
    }

    /** Ritorna true se si possono applicare le tasse al calcolo dei totali della fattura */
    fun canCalculateTaxes(): Boolean
    {
        return invoice.payment?.applyTaxes == true
                && invoice.payment?.vatPercentage != null
                && invoice.payment!!.vatPercentage!! > 0
    }

    /** Ritorna true se si può applicare un sconto al calcolo dei totali della fattura */
    fun canCalculateDiscount(): Boolean
    {
        return invoice.payment?.applyDiscount == true
                && invoice.payment?.discountPercentage != null
                && invoice.payment!!.discountPercentage!! > 0
    }

    private fun partial(): Double
    {
        return invoice.articles.sumByDouble { it.singleItemPrice * it.quantity }
    }

    private fun taxes(): Double
    {
        if (!canCalculateTaxes())
        {
            return 0.0
        }

        val amountToTax: Double =
            if (invoice.payment?.applyDiscount == true
                && invoice.payment?.discountPercentage != null
                && invoice.payment?.discountPercentage!! > 0)
            {
                // Se c'è da applicare lo sconto, tasso il totale parziale scontato
                partial() - discount()
            }
            else
            {
                // Se non c'è da applicare lo sconto, tasso il totale parziale
                partial()
            }

        return (amountToTax / 100) * (invoice.payment!!.vatPercentage!!)
    }

    private fun total(): Double
    {
        return partial() - discount() + taxes()
    }

    private fun discount(): Double
    {
        if (!canCalculateDiscount())
        {
            return 0.0
        }

        return (partial() / 100) * (invoice.payment!!.discountPercentage!!)
    }

    private fun isHeaderOk(): Boolean
    {
        // Il customer dev'essere presente e non vuoto
        return !invoice.customer!!.name.isNullOrBlank()
    }

    fun areArticlesOk(): Boolean
    {
        // Almeno un articolo inserito, altrimenti non fatturi nulla
        return invoice.articles.size > 0
    }

    /** Ritorna true se ci sono i dati minimi perché la fattura abbia un senso */
    fun isComplete(): Boolean
    {
        return isHeaderOk()
                && areArticlesOk()
    }

    /** Salva la fattura: se è nuova la aggiunge alla lista delle fatture */
    fun save()
    {
        if (isNewInvoice)
        {
            _invoicesRepository.add(invoice)
        }

        // Riapplico la formattazione dei prezzi degli articoli
        // per gestire il caso in cui mi viene cambiata la valuta senza ripassare dagli articoli
        generateFormattedArticleTotals()

        _invoicesRepository.save()
        isNewInvoice = false
    }

    private fun generateFormattedArticleTotals()
    {
        invoice.articles.forEach {
            it.formattedSingleItemPrice = _currencyFormat.format(it.singleItemPrice)
            it.formattedFinalAmount = _currencyFormat.format(it.quantity * it.singleItemPrice)
        }

    }

    /** Calcola tutti i totali della fattura e li formatta secondo la currency configurata */
    fun getFormattedTotals(): InvoiceTotals
    {
        var formattedDiscount: String? = null
        if (canCalculateDiscount())
        {
            formattedDiscount = "-${_currencyFormat.format(discount())}"
        }

        var formattedTaxes: String? = null
        if (canCalculateTaxes())
        {
            formattedTaxes = _currencyFormat.format(taxes())
        }

        return InvoiceTotals(
            formattedPartialAmount = _currencyFormat.format(partial()),
            formattedDiscountAmount = formattedDiscount,
            formattedTaxesAmount = formattedTaxes,
            formattedTotalAmount = _currencyFormat.format(total())
        )
    }

    fun getPdfFileName(): String
    {
        return Helpers.convertiStringaInNomeFile("Invoice ${invoice.code}.pdf")
    }


}