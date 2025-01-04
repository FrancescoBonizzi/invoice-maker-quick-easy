package com.francescobonizzi.invoiceEstimateReceiptMaker.domain

import android.content.Context
import android.text.format.DateFormat
import com.francescobonizzi.invoiceEstimateReceiptMaker.staticData.TemplatesData
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

data class AppConfiguration(
    val company: AppConfigurationCompany,
    val regionalSettings: AppConfigurationRegionalSettings,
    val template: AppConfigurationTemplate,
    val payments: AppConfigurationPayments
)
{
    companion object
    {
        fun createEmpty(context: Context): AppConfiguration
        {
            val defaultInvoiceColor = TemplatesData.getAllColors().first()

            return AppConfiguration(
                company = AppConfigurationCompany(),

                regionalSettings = AppConfigurationRegionalSettings(
                    iso4217currencyCode = getDefaultCurrencyCode(),
                    dateFormat = getDefaultDateFormat(context)),

                template = AppConfigurationTemplate(
                    layoutId = 0,
                    colorId = 0,
                    primaryColor = defaultInvoiceColor.primaryColor,
                    primaryColorLighter = defaultInvoiceColor.primaryColorLighter,
                    textColorOverPrimaryColor = defaultInvoiceColor.textColorOverPrimaryColor,
                    keyValueLabelColor = defaultInvoiceColor.keyValueLabelColor,
                    commonTextColor = defaultInvoiceColor.commonTextColor
                ),

                payments = AppConfigurationPayments(
                    vatLabel = "VAT",
                    isVATEnabled = false,
                    invoicePrefix = "INV-",
                    estimatePrefix = "QUOT-",
                    invoiceIdentityNumber = 1,
                    estimateIdentityNumber = 1,
                    invoiceUserDefinedLastId = 1,
                    estimateUserDefinedLastId = 1,
                    showInvoiceCode = true,
                    showEstimateCode = true,
                    showPaymentNotes = false,
                    showBankTransferDetails = false,
                    showInvoiceCreationDate = true,
                    showInvoiceExpirationDate = false,
                    showEstimateCreationDate = true,
                    showEstimateExpirationDate = false
                ))
        }

        private fun getDefaultDateFormat(context: Context): String
        {
            val systemDefaultDateFormat: Format = DateFormat.getDateFormat(context)
            return (systemDefaultDateFormat as SimpleDateFormat).toLocalizedPattern()
        }

        private fun getDefaultCurrencyCode(): String
        {
            return Currency.getInstance(Locale.getDefault()).currencyCode
        }
    }
}