package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices

import android.annotation.SuppressLint
import android.os.Bundle
import android.print.PrintHelpers
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FInvoicePreviewBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs.DialogPremium
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.TemplateLayout
import com.francescobonizzi.invoiceEstimateReceiptMaker.exceptions.JavascriptInvoiceRenderingException
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.BillingHandler
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.staticData.TemplatesData
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.InMemoryInvoicePreviewBundler
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IInvoicesRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class InvoicePreviewFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _templateLayouts: Array<TemplateLayout>
    private lateinit var _invoiceEditor: InvoiceEditor

    private val _logger: ILogger by inject()
    private val _previewInvoiceBundler: InMemoryInvoicePreviewBundler by inject()
    private val _invoicesRepository: IInvoicesRepository by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: FInvoicePreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        _binding = FInvoicePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        try
        {
            _navigationHost = requireView().findNavController()
            _templateLayouts = TemplatesData.getAllLayouts()

            setPageData()
            setHandlers()
        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_load))
            _navigationHost.popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.btnPrint.isEnabled = false
        binding.btnShare.isEnabled = false

        binding.progressBar.visibility = View.VISIBLE
        binding.webViewInvoiceContainer.visibility = View.INVISIBLE
        WebView.setWebContentsDebuggingEnabled(false)

        val showSharePrintButtons = arguments?.getBoolean("showSharePrintButtons")
        if (showSharePrintButtons == false)
        {
            binding.buttonsContainer.visibility = View.GONE
        }

        _invoiceEditor = _previewInvoiceBundler.get()

        binding.webViewInvoice.settings.javaScriptEnabled = true
        binding.webViewInvoice.setInitialScale(1)
        binding.webViewInvoice.settings.loadWithOverviewMode = true
        binding.webViewInvoice.settings.useWideViewPort = true
        binding.webViewInvoice.settings.allowFileAccess = true
        binding.webViewInvoice.settings.allowContentAccess = true

        binding.webViewInvoice.webViewClient = object : WebViewClient()
        {
            override fun onPageFinished(webView: WebView, url: String)
            {
                binding.btnPrint.isEnabled = true
                binding.btnShare.isEnabled = true
                binding.progressBar.visibility = View.GONE
                binding.webViewInvoiceContainer.visibility = View.VISIBLE

                binding.webViewInvoice.evaluateJavascript("document.getElementById('error-message').innerText") {
                    if (!it.isNullOrBlank() && it != "\"\"")
                    {
                        _logger.error(JavascriptInvoiceRenderingException(it))
                    }
                }
            }
        }

        val htmlInvoiceTemplate =
            requireActivity().assets.open(_templateLayouts[_invoiceEditor.invoice.template!!.layoutId!!].htmlName)
                .use { inputStream -> inputStream.bufferedReader().use { bufferedStream -> bufferedStream.readText() } }

        _invoiceEditor.invoice.shouldShowWatermark = !BillingHandler.isUnlimitedInvoicesPurchased()

        val htmlInvoiceTemplateFilled = HtmlHelpers.applyHtmlTemplate(
            htmlInvoiceTemplate,
            _appConfigurationRepository.get(),
            _invoiceEditor.invoice,
            _invoiceEditor.getFormattedTotals()
        )

        binding.webViewInvoice.loadDataWithBaseURL(
            null,
            htmlInvoiceTemplateFilled,
            "text/html; charset=utf-8",
            "UTF-8",
            null)

        // Se si tratta della preview di un'invoice vera (i pulsanti li ho solo dall'invoice vera),
        // e ha utilizzato abbastanza l'app, chiedo la review
        if (showSharePrintButtons == true
            && _invoicesRepository.getIdentity() >= AppReviewHelpers.MinInvoicesIdentityToAskForReview)
        {
            AppReviewHelpers.promptForInAppReview(requireActivity(), _logger)
        }
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            _navigationHost.popBackStack()
        }

        binding.btnShare.setOnClickListener {

            if (!BillingHandler.canCreateNewInvoices(_invoicesRepository))
            {
                val dialog = DialogPremium(requireActivity())
                dialog.show(parentFragmentManager, tag)
                return@setOnClickListener
            }

            // Analytics
            trackCurrentChoosenTemplate()

            // Alcuni utenti hanno segnalato errori nel creare il file temporaneo,
            // forse la cache non esiste
            Helpers.ensureCacheExistance(requireContext())

            // Cancello tutti i file dalla cache per essere sicuro di non accumularli
            Helpers.clearCacheFiles(requireContext())

            val filePath = "${requireContext().cacheDir.path}/${_invoiceEditor.getPdfFileName()}"

            PrintHelpers.printPdfToFile(
                activity = requireActivity(),
                context = requireContext(),
                webView = binding.webViewInvoice,
                filePath = filePath,
                onSuccess = {
                    // A volte onSuccess arriva prima che il file sia totalmente scritto sul disco
                    // di conseguenza, se provo a condividerlo, non viene trovato
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500) // Ritardo di 500ms

                        try
                        {
                            IntentHelpers.startIntentToShareFile(
                                fragment = this@InvoicePreviewFragment,
                                filePath = filePath,
                                fileNameForDialogTitle = "Invoice ${_invoiceEditor.invoice.code}"
                            )
                        }
                        catch (exception: Exception)
                        {
                            _logger.error(exception)
                            DialogsHelpers.showError(
                                requireActivity(),
                                exception.message ?: "Generic error")
                        }
                    }
                },
                onOperationFinished = {},
                onError = {
                    _logger.error(message = it)
                })
        }

        binding.btnPrint.setOnClickListener {

            if (!BillingHandler.canCreateNewInvoices(_invoicesRepository))
            {
                val dialog = DialogPremium(requireActivity())
                dialog.show(parentFragmentManager, tag)
                return@setOnClickListener
            }

            PrintHelpers.printWebViewContent(
                context = requireContext(),
                webView = binding.webViewInvoice,
                onOperationFinished = {})
        }
    }

    private fun trackCurrentChoosenTemplate()
    {
        val eventBundle = Bundle()
        eventBundle.putString("layoutId", _invoiceEditor.invoice.template!!.layoutId.toString())
        eventBundle.putString("colorId", _invoiceEditor.invoice.template!!.colorId.toString())
        _logger.event(requireContext(), "Template_Fattura_Condivisa", eventBundle)
    }


}