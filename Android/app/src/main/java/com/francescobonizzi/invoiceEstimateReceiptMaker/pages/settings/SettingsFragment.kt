package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.BuildConfig
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs.DialogPremium
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.AppReviewHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import org.koin.android.ext.android.inject
import java.util.*

class SettingsFragment : Fragment()
{
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()
    private lateinit var _navigationHost: NavController

    private var _binding: FSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
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
        binding.navigationTopBar.txtBack.text = getString(R.string.invoices)

        val appConfiguration = _appConfigurationRepository.get()
        binding.txtCompanyName.text = appConfiguration.company.name
        binding.txtMainEmail.text = appConfiguration.company.email1

        if (!appConfiguration.company.phone1.isNullOrBlank())
        {
            binding.txtMainPhoneNumber.text = PhoneNumberUtils.formatNumber(
                appConfiguration.company.phone1,
                Locale.getDefault().country
            )
        }

        binding.txtApplicationVersion.text = BuildConfig.VERSION_NAME
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnInvoicesSettings.setOnClickListener {
            _navigationHost.navigate(R.id.action_navigationSettingsFragment_to_invoicesSettingsFragment)
        }

        binding.btnCredits.setOnClickListener {
            _navigationHost.navigate(R.id.action_navigationProfileFragment_to_creditsFragment)
        }

        binding.btnWriteUs.setOnClickListener {
            try
            {
                val appName = getString(R.string.app_name)
                composeEmail(arrayOf("test@test.it"), "$appName feedback")
            }
            catch (exception: Exception)
            {
                _logger.error(exception)
            }
        }

        binding.btnRateUs.setOnClickListener {
            try
            {
                AppReviewHelpers.promptForReviewOnTheStore(requireActivity())
            }
            catch (exception: Exception)
            {
                _logger.error(exception)
            }
        }

        binding.btnPremium.setOnClickListener {
            val dialog = DialogPremium(requireActivity())
            dialog.show(parentFragmentManager, tag)
        }
    }

    private fun composeEmail(addresses: Array<String>, subject: String)
    {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (intent.resolveActivity(requireContext().packageManager) != null)
        {
            startActivity(intent)
        }
    }

}