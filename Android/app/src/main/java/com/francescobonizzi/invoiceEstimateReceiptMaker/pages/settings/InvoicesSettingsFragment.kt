package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsInvoicesBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers

class InvoicesSettingsFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private var _binding: FSettingsInvoicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        _binding = FSettingsInvoicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        _navigationHost = requireView().findNavController()
        setHandlers()
        setPageData()
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener { _navigationHost.popBackStack() }
        binding.btnRegionalSettings.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_regionalSettingsFragment) }
        binding.btnCompanyDetails.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_companySettingsFragment) }
        binding.btnPaymentDetails.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_paymentSettingsFragment) }
        binding.btnTaxes.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_taxSettingsFragment) }
        binding.btnDefaultNotes.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_defaultNotesFragment) }
        binding.btnInvoiceNumber.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_invoiceNumberFragment) }
        binding.btnLogoAndSignature.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_logoAndSignatureFragment) }
        binding.btnDates.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_datesFragment) }
        binding.btnDiscount.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_discountFragment) }
        binding.btnTemplates.setOnClickListener { _navigationHost.navigate(R.id.action_invoicesSettingsFragment_to_templatesFragment) }
    }

    private fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
    }
}