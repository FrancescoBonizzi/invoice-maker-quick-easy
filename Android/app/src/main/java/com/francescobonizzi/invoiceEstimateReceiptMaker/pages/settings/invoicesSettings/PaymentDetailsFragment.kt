package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings.invoicesSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsInvoicesPaymentBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject

class PaymentDetailsFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: FSettingsInvoicesPaymentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FSettingsInvoicesPaymentBinding.inflate(inflater, container, false)
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
        binding.txtPayPalEmail.setText(appConfiguration.payments.payPalEmail)
        binding.txtCheckHolder.setText(appConfiguration.payments.checkHolder)
        binding.txtBankDetails.setText(appConfiguration.payments.bankDetails)
        binding.txtPaymentNotes.setText(appConfiguration.payments.paymentNotes)
        binding.chkShowBankTransferDetails.isChecked = appConfiguration.payments.showBankTransferDetails
        binding.chkShowPaymentNotes.isChecked = appConfiguration.payments.showPaymentNotes
        binding.chkShowPayPalEmail.isChecked = appConfiguration.payments.showPayPalEmail
        binding.chkShowCash.isChecked = appConfiguration.payments.showCash
        binding.chkShowCreditCard.isChecked = appConfiguration.payments.showCreditCard
        binding.chkShowDebitCard.isChecked = appConfiguration.payments.showDebitCard
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            _navigationHost.popBackStack()
        }

        binding.txtCheckHolder.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtBankDetails.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtPayPalEmail.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtPaymentNotes.doOnTextChanged { _, _, _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowBankTransferDetails.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowPaymentNotes.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowPayPalEmail.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowCash.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowCreditCard.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowDebitCard.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {
            try
            {
                val appConfiguration = _appConfigurationRepository.get()
                appConfiguration.payments.payPalEmail = binding.txtPayPalEmail.text()
                appConfiguration.payments.checkHolder = binding.txtCheckHolder.text()
                appConfiguration.payments.bankDetails = binding.txtBankDetails.text()
                appConfiguration.payments.paymentNotes = binding.txtPaymentNotes.text.toString()
                appConfiguration.payments.showBankTransferDetails =
                    binding.chkShowBankTransferDetails.isChecked
                appConfiguration.payments.showPaymentNotes = binding.chkShowPaymentNotes.isChecked
                appConfiguration.payments.showPayPalEmail = binding.chkShowPayPalEmail.isChecked
                appConfiguration.payments.showCash = binding.chkShowCash.isChecked
                appConfiguration.payments.showCreditCard = binding.chkShowCreditCard.isChecked
                appConfiguration.payments.showDebitCard = binding.chkShowDebitCard.isChecked

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
