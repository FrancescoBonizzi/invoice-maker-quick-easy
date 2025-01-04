package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings.invoicesSettings

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsInvoicesCompanyBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject
import java.util.*

class CompanyDetailsFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: FSettingsInvoicesCompanyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FSettingsInvoicesCompanyBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = false

        val appConfiguration = _appConfigurationRepository.get()
        binding.txtCompanyName.setText(appConfiguration.company.name)
        binding.txtCompanyHolderName.setText(appConfiguration.company.holderName)
        binding.txtVATNumber.setText(appConfiguration.company.vatNumber)
        binding.txtAddress1.setText(appConfiguration.company.address1)

        if (!appConfiguration.company.phone1.isNullOrBlank())
        {
            binding.txtPhone1.setText(
                PhoneNumberUtils.formatNumber(
                    appConfiguration.company.phone1,
                    Locale.getDefault().country))
        }

        if (!appConfiguration.company.phone2.isNullOrBlank())
        {
            binding.txtPhone2.setText(
                PhoneNumberUtils.formatNumber(
                    appConfiguration.company.phone2,
                    Locale.getDefault().country))
        }

        binding.txtEmail1.setText(appConfiguration.company.email1)
        binding.txtWebsite.setText(appConfiguration.company.webSite)
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            _navigationHost.popBackStack()
        }

        binding.txtCompanyName.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtCompanyHolderName.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtVATNumber.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtAddress1.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtPhone1.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtPhone2.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtWebsite.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowYourCompanyDetails.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.txtEmail1.doOnTextChanged {
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {

            try
            {
                val appConfiguration = _appConfigurationRepository.get()

                appConfiguration.company.name = binding.txtCompanyName.text()
                appConfiguration.company.holderName = binding.txtCompanyHolderName.text()
                appConfiguration.company.vatNumber = binding.txtVATNumber.text()
                appConfiguration.company.address1 = binding.txtAddress1.text()
                appConfiguration.company.phone1 = binding.txtPhone1.text()
                appConfiguration.company.phone2 = binding.txtPhone2.text()
                appConfiguration.company.email1 = binding.txtEmail1.text()
                appConfiguration.company.webSite = binding.txtWebsite.text()
                appConfiguration.company.showCompanyDetails = binding.chkShowYourCompanyDetails.isChecked

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