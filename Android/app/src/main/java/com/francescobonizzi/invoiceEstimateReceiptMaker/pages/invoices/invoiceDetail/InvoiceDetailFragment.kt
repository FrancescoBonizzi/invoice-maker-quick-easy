package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FInvoiceDetailBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.InMemoryInvoicePreviewBundler
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Constants
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class InvoiceDetailFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _dateFormatter: SimpleDateFormat
    private lateinit var _calendar: Calendar

    private lateinit var _currentInvoiceEditor: InvoiceEditor

    private val _appConfigurationRepository: IAppConfigurationRepository by inject()
    private val _previewInvoiceBundler: InMemoryInvoicePreviewBundler by inject()

    private val _logger: ILogger by inject()

    private var _binding: FInvoiceDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FInvoiceDetailBinding.inflate(inflater, container, false)
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
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarBackgroundColor)

        try
        {
            _navigationHost = requireView().findNavController()
            val appConfiguration = _appConfigurationRepository.get()
            _dateFormatter = Helpers.getDateFormatter(appConfiguration)

            _currentInvoiceEditor = (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor()

            setHandlers()
            setPageData()
        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_load))
            _navigationHost.popBackStack()
        }
    }

    fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = getString(R.string.invoices)
        binding.txtCreationDate.text = _dateFormatter.format(_currentInvoiceEditor.invoice.creationDate!!)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE

        if (!_currentInvoiceEditor.isNewInvoice)
        {
            binding.txtTitle.text = getString(R.string.invoice_detail)
        }

        binding.chkPaid.isChecked = _currentInvoiceEditor.invoice.paymentDate != null

        // Elenco clienti
        binding.txtCustomerName.text = _currentInvoiceEditor.invoice.customer!!.name

        // Computi
        val totals = _currentInvoiceEditor.getFormattedTotals()

        // Computo sconto
        if (totals.formattedDiscountAmount != null)
        {
            binding.panelDiscount.visibility = View.VISIBLE
            binding.txtDiscountPercentageLabel.text =
                getString(R.string.discount_amount).format(_currentInvoiceEditor.invoice.payment!!.discountPercentage!!)
            binding.txtDiscountAmount.text = totals.formattedDiscountAmount
        }
        else
        {
            binding.panelDiscount.visibility = View.GONE
        }

        // Computo tasse
        var vatLabel = ""
        if (!_currentInvoiceEditor.invoice.payment!!.vatLabel.isNullOrBlank())
        {
            vatLabel = _currentInvoiceEditor.invoice.payment!!.vatLabel!!
        }

        if (totals.formattedTaxesAmount != null)
        {
            binding.panelTaxes.visibility = View.VISIBLE
            binding.txtTaxPercentageLabel.text =
                getString(R.string.tax_amount).format(
                    vatLabel,
                    _currentInvoiceEditor.invoice.payment!!.vatPercentage!!)
            binding.txtTaxAmount.text = totals.formattedTaxesAmount
        }
        else
        {
            binding.panelTaxes.visibility = View.GONE
        }

        // Computo parziale
        binding.txtPartialSum.text = totals.formattedPartialAmount

        // Computo totale
        binding.txtTotal.text = totals.formattedTotalAmount

        // Abilitazioni varie in base allo stato di completamento della fattura
        refreshLayoutForInvoiceChange()
    }

    private fun refreshLayoutForInvoiceChange()
    {
        if (_currentInvoiceEditor.areArticlesOk())
        {
            binding.panelPartial.visibility = View.VISIBLE

            if (_currentInvoiceEditor.canCalculateDiscount())
            {
                binding.panelDiscount.visibility = View.VISIBLE
            }

            if (_currentInvoiceEditor.canCalculateTaxes())
            {
                binding.panelTaxes.visibility = View.VISIBLE
            }

            binding.panelTotal.visibility = View.VISIBLE
            binding.txtNoArticlesInTotals.visibility = View.GONE
        }
        else
        {
            binding.panelPartial.visibility = View.GONE
            binding.panelDiscount.visibility = View.GONE
            binding.panelTaxes.visibility = View.GONE
            binding.panelTotal.visibility = View.GONE
            binding.txtNoArticlesInTotals.visibility = View.VISIBLE
        }
    }

    private fun save()
    {
        try
        {
            _currentInvoiceEditor.invoice.creationDate =
                _dateFormatter.parse(binding.txtCreationDate.text.toString())
            _currentInvoiceEditor.invoice.customer!!.name = binding.txtCustomerName.text.toString()
            _currentInvoiceEditor.save()
        }
        catch (ex: Exception)
        {
            _logger.error(ex)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_save))
        }
    }

    fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        callback.isEnabled = true

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {
            save()
            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            }

            builder?.setMessage(R.string.invoice_saved)
                ?.setTitle(R.string.invoice_saved_title)

            builder?.apply {
                setPositiveButton("Ok") { dialog, _ ->
                    dialog.cancel()
                }
            }

            val dialog: AlertDialog? = builder?.create()
            dialog?.show()
        }

        binding.txtCustomerName.doOnTextChanged { text, _, _, _ ->
            _currentInvoiceEditor.invoice.customer!!.name = text.toString()
            refreshLayoutForInvoiceChange()
        }

        binding.customerChooser.setOnClickListener {

            val bundle = Bundle()
            bundle.putBoolean(Constants.IsForInvoiceBundleVariable, true)
            _navigationHost.navigate(R.id.action_invoiceDetailFragment_to_customersFragment, bundle)

        }

        _calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            _calendar.set(Calendar.YEAR, year)
            _calendar.set(Calendar.MONTH, month)
            _calendar.set(Calendar.DAY_OF_MONTH, day)
            binding.txtCreationDate.text = _dateFormatter.format(_calendar.time)
        }

        binding.txtCreationDate.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetListener,
                _calendar.get(Calendar.YEAR),
                _calendar.get(Calendar.MONTH),
                _calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.chkPaid.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                _currentInvoiceEditor.invoice.paymentDate = Date()
            }
            else
            {
                _currentInvoiceEditor.invoice.paymentDate = null
            }
        }

        binding.btnInvoiceHeader.setOnClickListener { _navigationHost.navigate(R.id.action_invoiceDetailFragment_to_invoiceDetailHeaderFragment) }
        binding.btnInvoicePayment.setOnClickListener { _navigationHost.navigate(R.id.action_invoiceDetailFragment_to_invoiceDetailPaymentFragment) }
        binding.btnInvoiceProducts.setOnClickListener { _navigationHost.navigate(R.id.action_invoiceDetailFragment_to_invoiceDetailArticlesSummaryFragment) }
        binding.btnInvoiceFooter.setOnClickListener { _navigationHost.navigate(R.id.action_invoiceDetailFragment_to_invoiceDetailFooterFragment) }

        binding.btnInvoiceTemplate.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.IsForInvoiceBundleVariable, true)
            _navigationHost.navigate(R.id.action_invoiceDetailFragment_to_templatesFragment2, bundle)
        }

        // Creo il file nella cache dell'applicazione, faccio lo share, pulisco la cache
        binding.btnPreviewAndShare.setOnClickListener {

            try
            {
                // Ne approfitto per salvare: se uno vuole andare alla stampa, la fattura va bene
                save()

                if (!_currentInvoiceEditor.isComplete())
                {
                    val builder: AlertDialog.Builder? = activity?.let {
                        AlertDialog.Builder(it)
                    }

                    builder?.setMessage(R.string.incomplete_invoice_text)
                        ?.setTitle(R.string.incomplete_invoice_title)

                    builder?.apply {
                        setPositiveButton("Ok") { dialog, _ ->
                            dialog.cancel()
                        }
                    }

                    val dialog: AlertDialog? = builder?.create()
                    dialog?.show()

                    return@setOnClickListener
                }

                _previewInvoiceBundler.set(_currentInvoiceEditor)
                val bundle = Bundle()
                bundle.putBoolean(Constants.ShowSharePrintButtonsVariable, true)
                _navigationHost.navigate(
                    R.id.action_invoiceDetailFragment_to_invoicePreviewFragment2,
                    bundle)
            }
            catch (exception: Exception)
            {
                _logger.error(exception)
                DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error))
            }
        }

    }

}