package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings.invoicesSettings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsInvoicesLogoSignatureBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.IntentHelpers
import org.koin.android.ext.android.inject

class LogoAndSignatureFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _currentLogoPath: String = ""
    private var _currentSignaturePath: String = ""

    private var _binding: FSettingsInvoicesLogoSignatureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FSettingsInvoicesLogoSignatureBinding.inflate(inflater, container, false)
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

        if (!appConfiguration.company.logoImagePath.isNullOrEmpty())
        {
            _currentLogoPath = appConfiguration.company.logoImagePath!!
            val logoImage = Helpers.getBitmap(_currentLogoPath)
            binding.imgCompanyLogo.setImageBitmap(logoImage)
        }

        if (!appConfiguration.company.signatureImagePath.isNullOrEmpty())
        {
            _currentSignaturePath = appConfiguration.company.signatureImagePath!!
            val signatureImage = Helpers.getBitmap(_currentSignaturePath)
            binding.imgSignature.setImageBitmap(signatureImage)
        }

        binding.chkShowYourCompanyLogo.isChecked = appConfiguration.company.showCompanyLogo
        binding.chkShowSignature.isChecked = appConfiguration.company.showSignature
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            _navigationHost.popBackStack()
        }

        binding.logoContainer.setOnClickListener {
            IntentHelpers.startIntentToPickImage(this, IntentHelpers.IntentPickLogoCode)
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.signatureContainer.setOnClickListener {
            IntentHelpers.startIntentToPickImage(this, IntentHelpers.IntentPickSignatureCode)
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowYourCompanyLogo.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.chkShowSignature.setOnCheckedChangeListener { _, _ ->
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {
            try
            {
                val appConfiguration = _appConfigurationRepository.get()

                appConfiguration.company.logoImagePath = _currentLogoPath
                appConfiguration.company.signatureImagePath = _currentSignaturePath
                appConfiguration.company.showCompanyLogo = binding.chkShowYourCompanyLogo.isChecked
                appConfiguration.company.showSignature = binding.chkShowSignature.isChecked

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        try
        {

            val pickImageResult = IntentHelpers.getPathOnPickImageIntentResult(
                requireContext(),
                requestCode,
                resultCode,
                data)
                ?: return

            if (pickImageResult.selectedImageType == IntentHelpers.IntentPickLogoCode)
            {
                binding.imgCompanyLogo.setImageBitmap(pickImageResult.image)
                _currentLogoPath = pickImageResult.imagePath
            }
            else if (pickImageResult.selectedImageType == IntentHelpers.IntentPickSignatureCode)
            {
                binding.imgSignature.setImageBitmap(pickImageResult.image)
                _currentSignaturePath = pickImageResult.imagePath
            }

        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error))
        }
    }

}