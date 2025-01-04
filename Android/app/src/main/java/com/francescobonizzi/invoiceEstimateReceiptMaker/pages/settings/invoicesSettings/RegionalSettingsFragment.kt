package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings.invoicesSettings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.adapters.CurrencyAdapter
import com.francescobonizzi.invoiceEstimateReceiptMaker.adapters.DateFormatAdapter
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsInvoicesRegionBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.AppConfiguration
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject
import java.util.*

class RegionalSettingsFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _defaultLocale: Locale
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: FSettingsInvoicesRegionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FSettingsInvoicesRegionBinding.inflate(inflater, container, false)
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
            _defaultLocale = Locale.getDefault()

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

    @SuppressLint("RestrictedApi")
    private fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = false

        val appConfiguration = _appConfigurationRepository.get()
        manageCurrency(appConfiguration)
        manageDateFormat(appConfiguration)
    }

    private fun manageCurrency(appConfiguration: AppConfiguration)
    {
        if (appConfiguration.regionalSettings.iso4217currencyCode.isNullOrBlank())
        {
            throw Exception("Default currency not set")
        }

        val currencies = Currency.getAvailableCurrencies()
            .sortedBy { i -> i.currencyCode }
        val adapter = CurrencyAdapter(
            requireActivity(),
            R.layout.ad_spinner_item_currency,
            currencies
        )
        binding.cmbCurrency.adapter = adapter

        binding.cmbCurrency.setSelection(
            currencies.indexOf(
                currencies.first { it.currencyCode == appConfiguration.regionalSettings.iso4217currencyCode })
        )
    }

    private fun manageDateFormat(appConfiguration: AppConfiguration)
    {
        if (appConfiguration.regionalSettings.dateFormat.isNullOrBlank())
        {
            throw Exception("Default date format not set")
        }

        val dateFormats = arrayListOf(
            "yyyy-MM-dd",
            "dd-MM-yyyy",
            "MM-dd-yyyy",
            "yyyy/MM/dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy",
            "yyyy.MM.dd",
            "dd.MM.yyyy",
            "MM.dd.yyyy",
            "MMMM d, yyyy",
            "d MMM yyyy"
        )

        // Se il dateFormat selezionato non è in questa lista, lo aggiungo.
        // (Di default il dateFormat selezionato è quello di sistema, e potrei non averlo incluso)
        if (!dateFormats.contains(appConfiguration.regionalSettings.dateFormat))
        {
            dateFormats.add(appConfiguration.regionalSettings.dateFormat!!)
        }

        val adapter = DateFormatAdapter(
            requireActivity(),
            R.layout.ad_spinner_item_currency,
            dateFormats
        )

        binding.cmbDateFormat.adapter = adapter

        binding.cmbDateFormat.setSelection(
            dateFormats.indexOf(
                dateFormats.firstOrNull { it == appConfiguration.regionalSettings.dateFormat }
            )
        )
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            _navigationHost.popBackStack()
        }

        // Devo farlo perché l'handler viene scatenato subito appena si apre la pagina
        // e mi riattiva subito il pulsante salva
        var cmbCurrencyFirstAutomaticSelection = true
        binding.cmbCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(parent: AdapterView<*>?)
            {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long)
            {
                if (!cmbCurrencyFirstAutomaticSelection)
                {
                    binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
                }
                else
                {
                    cmbCurrencyFirstAutomaticSelection = false
                }
            }
        }

        // Devo farlo perché l'handler viene scatenato subito appena si apre la pagina
        // e mi riattiva subito il pulsante salva
        var cmbDateFormatFirstAutomaticSelection = true
        binding.cmbDateFormat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(parent: AdapterView<*>?)
            {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long)
            {
                if (!cmbDateFormatFirstAutomaticSelection)
                {
                    binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
                }
                else
                {
                    cmbDateFormatFirstAutomaticSelection = false
                }
            }
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {
            try
            {
                val appConfiguration = _appConfigurationRepository.get()

                val selectedCurrency: Currency = binding.cmbCurrency.selectedItem as Currency
                appConfiguration.regionalSettings.iso4217currencyCode =
                    selectedCurrency.currencyCode

                val selectedDateFormat = binding.cmbDateFormat.selectedItem as String
                appConfiguration.regionalSettings.dateFormat = selectedDateFormat

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