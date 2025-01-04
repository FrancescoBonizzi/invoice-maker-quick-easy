package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FInvoiceDetailPaymentBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject

class InvoiceDetailPaymentFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _currentInvoiceEditor: InvoiceEditor
    private val _logger: ILogger by inject()

    private var _binding: FInvoiceDetailPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FInvoiceDetailPaymentBinding.inflate(inflater, container, false)
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
            _currentInvoiceEditor =
                (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor()

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

    fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)

        binding.chkApplyTaxes.isChecked = _currentInvoiceEditor.invoice.payment!!.applyTaxes
        binding.txtTaxLabel.setText(_currentInvoiceEditor.invoice.payment!!.vatLabel)

        if (_currentInvoiceEditor.invoice.payment!!.vatPercentage != null)
        {
            binding.txtTaxPercentage.setText(_currentInvoiceEditor.invoice.payment!!.vatPercentage.toString())
        }

        binding.chkApplyDiscount.isChecked = _currentInvoiceEditor.invoice.payment!!.applyDiscount

        if (_currentInvoiceEditor.invoice.payment!!.discountPercentage != null)
        {
            binding.txtDiscountPercentage.setText(_currentInvoiceEditor.invoice.payment!!.discountPercentage.toString())
        }

        binding.chkShowPayPalEmail.isChecked = _currentInvoiceEditor.invoice.payment!!.showPayPalEmail
        binding.txtPayPalEmail.setText(_currentInvoiceEditor.invoice.payment!!.payPalEmail)

        binding.chkShowBankTransferDetails.isChecked =
            _currentInvoiceEditor.invoice.payment!!.showBankTransferDetails
        binding.txtCheckHolder.setText(_currentInvoiceEditor.invoice.payment!!.checkHolder)
        binding.txtBankDetails.setText(_currentInvoiceEditor.invoice.payment!!.bankDetails)

        binding.chkShowPaymentNotes.isChecked = _currentInvoiceEditor.invoice.payment!!.showPaymentNotes
        binding.txtPaymentNotes.setText(_currentInvoiceEditor.invoice.payment!!.paymentNotes)

        binding.chkShowCash.isChecked = _currentInvoiceEditor.invoice.payment!!.showCash
        binding.chkShowCreditCard.isChecked = _currentInvoiceEditor.invoice.payment!!.showCreditCard
        binding.chkShowDebitCard.isChecked = _currentInvoiceEditor.invoice.payment!!.showDebitCard
    }

    private fun back()
    {
        try
        {
            _currentInvoiceEditor.invoice.payment!!.applyTaxes = binding.chkApplyTaxes.isChecked
            _currentInvoiceEditor.invoice.payment!!.vatLabel = binding.txtTaxLabel.text()
            _currentInvoiceEditor.invoice.payment!!.vatPercentage = Helpers.textToDoubleOrNull(binding.txtTaxPercentage.text())

            _currentInvoiceEditor.invoice.payment!!.applyDiscount = binding.chkApplyDiscount.isChecked
            _currentInvoiceEditor.invoice.payment!!.discountPercentage = Helpers.textToDoubleOrNull(binding.txtDiscountPercentage.text())

            _currentInvoiceEditor.invoice.payment!!.showPayPalEmail = binding.chkShowPayPalEmail.isChecked
            _currentInvoiceEditor.invoice.payment!!.payPalEmail = binding.txtPayPalEmail.text()

            _currentInvoiceEditor.invoice.payment!!.showBankTransferDetails = binding.chkShowBankTransferDetails.isChecked
            _currentInvoiceEditor.invoice.payment!!.checkHolder = binding.txtCheckHolder.text()
            _currentInvoiceEditor.invoice.payment!!.bankDetails = binding.txtBankDetails.text()

            _currentInvoiceEditor.invoice.payment!!.showPaymentNotes = binding.chkShowPaymentNotes.isChecked
            _currentInvoiceEditor.invoice.payment!!.paymentNotes = binding.txtPaymentNotes.text.toString()

            _currentInvoiceEditor.invoice.payment!!.showCash = binding.chkShowCash.isChecked
            _currentInvoiceEditor.invoice.payment!!.showCreditCard = binding.chkShowCreditCard.isChecked
            _currentInvoiceEditor.invoice.payment!!.showDebitCard = binding.chkShowDebitCard.isChecked
        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_save))
        }

        _navigationHost.popBackStack()
    }

    fun setHandlers()
    {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.navigationTopBar.btnBack.isEnabled)
            {
                back()
            }
        }
        callback.isEnabled = true

        binding.navigationTopBar.btnBack.setOnClickListener {
            back()
        }

        binding.txtPayPalEmail.doOnTextChanged {

            if (binding.txtPayPalEmail.text().isNotEmpty())
            {
                if (Helpers.isValidEmail(binding.txtPayPalEmail.text()))
                {
                    binding.txtPayPalEmail.clearErrors()
                    binding.navigationTopBar.btnBack.isEnabled = true
                }
                else
                {
                    binding.txtPayPalEmail.setError(
                        getString(R.string.validation_invalid_email_address))
                    binding.navigationTopBar.btnBack.isEnabled = false
                }
            }
            else
            {
                binding.txtPayPalEmail.clearErrors()
                binding.navigationTopBar.btnBack.isEnabled = true
            }

        }

    }
}