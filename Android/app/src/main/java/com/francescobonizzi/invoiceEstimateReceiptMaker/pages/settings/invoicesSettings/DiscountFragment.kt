package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings.invoicesSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsInvoicesDiscountBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject

class DiscountFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: FSettingsInvoicesDiscountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FSettingsInvoicesDiscountBinding.inflate(inflater, container, false)
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

        if (appConfiguration.payments.discountPercentage != null)
        {
            binding.txtDiscountPercentage.setText(appConfiguration.payments.discountPercentage.toString())
        }

        binding.chkApplyDiscount.isChecked = appConfiguration.payments.applyDiscount
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            _navigationHost.popBackStack()
        }

        binding.txtDiscountPercentage.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkApplyDiscount.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {
            try
            {
                val appConfiguration = _appConfigurationRepository.get()

                appConfiguration.payments.discountPercentage = Helpers.textToDoubleOrNull(binding.txtDiscountPercentage.text())
                appConfiguration.payments.applyDiscount = binding.chkApplyDiscount.isChecked

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