package com.francescobonizzi.invoiceEstimateReceiptMaker.staticData

import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.*
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import java.util.*

class Fakes
{
    companion object
    {
        fun createFakeInvoice(
            layoutId: Int,
            colorId: Int,
            templateColor: TemplateColor,
            regionalSettings: AppConfigurationRegionalSettings): Invoice
        {
            val currencyFormat = Helpers.getCurrencyFormat(regionalSettings)

            return Invoice(
                code = "EX-001",
                userDefinedId = 1,
                creationDate = Date(),
                expirationDate = null,
                customer = InvoiceCustomer(
                    showCustomerName = true,
                    name = "Example customer",
                    address = "",
                    email = "",
                    phone = ""
                ),
                company = InvoiceCompany(
                    showCompanyDetails = true,
                    showCompanyLogo = false,
                    showSignature = false,
                    name = "Example company",
                    holderName = "Example holder name",
                    vatNumber = "Example VAT number",
                    address1 = "Example address",
                    phone1 = "12345678",
                    phone2 = "",
                    email1 = "email@emails.com",
                    webSite = ""
                ),

                regionalSettings = regionalSettings,

                articles = mutableListOf(
                    InvoiceArticle(
                        name = "Article 1",
                        quantity = 2,
                        description = "",
                        singleItemPrice = 12.0,
                        formattedSingleItemPrice = currencyFormat.format(12),
                        formattedFinalAmount = currencyFormat.format(24)),
                    InvoiceArticle(
                        name = "Article 2",
                        quantity = 2,
                        description = "",
                        singleItemPrice = 10.0,
                        formattedSingleItemPrice = currencyFormat.format(10),
                        currencyFormat.format(20)),
                    InvoiceArticle(
                        name = "Article 3",
                        quantity = 1,
                        description = "",
                        singleItemPrice = 9.0,
                        formattedSingleItemPrice = currencyFormat.format(9),
                        currencyFormat.format(9))
                ),

                template = InvoiceTemplate(
                    layoutId = layoutId,
                    colorId = colorId,
                    primaryColor = templateColor.primaryColor,
                    primaryColorLighter = templateColor.primaryColorLighter,
                    textColorOverPrimaryColor = templateColor.textColorOverPrimaryColor,
                    keyValueLabelColor = templateColor.keyValueLabelColor,
                    commonTextColor = templateColor.commonTextColor
                ),

                payment = InvoicePayment(
                    applyTaxes = true,
                    applyDiscount = false,
                    showPayPalEmail = true,
                    showBankTransferDetails = true,
                    showPaymentNotes = false,
                    payPalEmail = "example@example.it",
                    bankDetails = "Example bank name",
                    checkHolder = "Example check holder name",
                    vatPercentage = 20.0,
                    vatLabel = "VAT",
                    paymentNotes = "",
                    discountPercentage = null),

                showInvoiceCode = true,
                showInvoiceCreationDate = true,
                showInvoiceExpirationDate = false,
                footerNotes = "This is just an example invoice preview",
                showFooterNotes = true)
        }
    }
}