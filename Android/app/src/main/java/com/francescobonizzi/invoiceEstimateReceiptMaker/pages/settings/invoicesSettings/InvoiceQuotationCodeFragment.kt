package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings.invoicesSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsInvoicesInvoiceQuotationCodeBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject

class InvoiceQuotationCodeFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: FSettingsInvoicesInvoiceQuotationCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        _binding = FSettingsInvoicesInvoiceQuotationCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        try
        {
            _navigationHost = requireView().findNavController()

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

    private fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = false

        val appConfiguration = _appConfigurationRepository.get()
        binding.txtInvoicePrefix.setText(appConfiguration.payments.invoicePrefix)
        binding.txtInvoiceStartingNumber.setText(appConfiguration.payments.invoiceUserDefinedLastId.toString())
        binding.chkShowInvoiceCode.isChecked = appConfiguration.payments.showInvoiceCode

        refreshInvoiceCodeExample()
    }

    private fun refreshInvoiceCodeExample()
    {
        binding.txtInvoiceCodeExample.setText(
            binding.txtInvoicePrefix.text() +
                    binding.txtInvoiceStartingNumber.text())
    }

    private fun setHandlers()
    {
        binding.txtInvoicePrefix.doOnTextChanged {
            refreshInvoiceCodeExample()
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }
        binding.txtInvoiceStartingNumber.doOnTextChanged {
            refreshInvoiceCodeExample()
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowInvoiceCode.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.navigationTopBar.btnBack.setOnClickListener {
            _navigationHost.popBackStack()
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {
            try
            {
                if (binding.txtInvoiceStartingNumber.text().isBlank())
                {
                    binding.txtInvoiceStartingNumber.setText("0")
                }

                if (binding.txtInvoicePrefix.text().isBlank())
                {
                    binding.txtInvoicePrefix.setText("INV-")
                }

                val appConfiguration = _appConfigurationRepository.get()
                appConfiguration.payments.invoicePrefix = binding.txtInvoicePrefix.text()
                appConfiguration.payments.invoiceUserDefinedLastId =
                    binding.txtInvoiceStartingNumber.text().toInt()
                appConfiguration.payments.showInvoiceCode = binding.chkShowInvoiceCode.isChecked

                _appConfigurationRepository.save()

            }
            catch (exception: Exception)
            {
                _logger.error(exception)
                DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_save))
            }

            _navigationHost.popBackStack()
        }

    }

}