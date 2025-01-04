@file:Suppress(
    "FoldInitializerAndIfToElvis", "FoldInitializerAndIfToElvis",
    "FoldInitializerAndIfToElvis")

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
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FInvoiceDetailFooterBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject

class InvoiceDetailFooterFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _currentInvoiceEditor: InvoiceEditor

    private val _logger: ILogger by inject()

    private var _binding: FInvoiceDetailFooterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FInvoiceDetailFooterBinding.inflate(inflater, container, false)
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
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)

        binding.chkShowSignature.isChecked = _currentInvoiceEditor.invoice.company!!.showSignature
        binding.txtDefaultNotesInvoice.setText(_currentInvoiceEditor.invoice.footerNotes)
        binding.chkShowNotes.isChecked = _currentInvoiceEditor.invoice.showFooterNotes
    }

    private fun back()
    {
        try
        {
            _currentInvoiceEditor.invoice.company!!.showSignature = binding.chkShowSignature.isChecked
            _currentInvoiceEditor.invoice.footerNotes = binding.txtDefaultNotesInvoice.text.toString()
            _currentInvoiceEditor.invoice.showFooterNotes = binding.chkShowNotes.isChecked
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
            back()
        }
        callback.isEnabled = true

        binding.navigationTopBar.btnBack.setOnClickListener {
            back()
        }
    }

}