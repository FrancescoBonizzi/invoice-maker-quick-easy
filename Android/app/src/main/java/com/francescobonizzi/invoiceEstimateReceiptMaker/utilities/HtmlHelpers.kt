@file:Suppress("unused")

package com.francescobonizzi.invoiceEstimateReceiptMaker.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.AppConfiguration
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.InvoiceTotals
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/** Metodi di comodo per gestire le stringhe html */
class HtmlHelpers
{
    companion object
    {
        /** Generates a pdf file from an html page */
        @SuppressLint("SetJavaScriptEnabled")
        private fun generatePdfFromHtml(
            context: Context,
            htmlContent: String,
            onPdfCreated: (webView: WebView) -> Unit)
        {
            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = object : WebViewClient()
            {
                override fun onPageFinished(webView: WebView, url: String)
                {
                    onPdfCreated(webView)
                }
            }

            webView.loadDataWithBaseURL(
                null,
                htmlContent,
                "text/html; charset=utf-8",
                "UTF-8",
                null)
        }

        /** Takes an invoice and loads it into an html/javascript template */
        fun applyHtmlTemplate(
            htmlTemplate: String,
            appConfiguration: AppConfiguration,
            invoice: Invoice,
            invoiceTotals: InvoiceTotals): String
        {
            val jsonSerializer: Gson = GsonBuilder()
                .setDateFormat(appConfiguration.regionalSettings.dateFormat)
                .create()

            val jsonInvoice = jsonSerializer.toJson(jsonSerializer.toJson(invoice))

            var htmlTemplateReplaced = htmlTemplate.replace(
                oldValue = "'#EASTER_EGG_JSON#'",
                newValue = jsonInvoice)

            val jsonInvoiceTotals = jsonSerializer.toJson((jsonSerializer.toJson(invoiceTotals)))

            htmlTemplateReplaced = htmlTemplateReplaced.replace(
                oldValue = "'#EASTER_EGG_JSON_TOTALS#'",
                newValue = jsonInvoiceTotals)

            htmlTemplateReplaced = if (!appConfiguration.company.logoImagePath.isNullOrBlank())
            {
                htmlTemplateReplaced.replace(
                    oldValue = "#EASTER_EGG_LOGO#",
                    newValue = "file://" + appConfiguration.company.logoImagePath)
            }
            else
            {
                htmlTemplateReplaced.replace(
                    oldValue = "#EASTER_EGG_LOGO#",
                    newValue = "")
            }

            htmlTemplateReplaced = if (!appConfiguration.company.signatureImagePath.isNullOrBlank())
            {
                htmlTemplateReplaced.replace(
                    oldValue = "#EASTER_EGG_SIGNATURE#",
                    newValue = "file://" + appConfiguration.company.signatureImagePath)
            }
            else
            {
                htmlTemplateReplaced.replace(
                    oldValue = "#EASTER_EGG_SIGNATURE#",
                    newValue = "")
            }

            htmlTemplateReplaced = htmlTemplateReplaced.replace(
                oldValue = "var templateDebugMode = true;",
                newValue = "var templateDebugMode = false;")

            htmlTemplateReplaced = htmlTemplateReplaced.replace(
                oldValue = "templateLoad.js",
                newValue = "file:///android_asset/templateLoad.js")

            htmlTemplateReplaced = htmlTemplateReplaced.replace(
                oldValue = "watermark.png",
                newValue = "file:///android_asset/watermark.png")

            return htmlTemplateReplaced
        }
    }
}
