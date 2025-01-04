@file:Suppress("FoldInitializerAndIfToElvis", "FoldInitializerAndIfToElvis")

package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail

import android.app.DatePickerDialog
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FInvoiceDetailHeaderBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class InvoiceDetailHeaderFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _dateFormatter: SimpleDateFormat
    private lateinit var _calendar: Calendar
    private lateinit var _currentInvoiceEditor: InvoiceEditor

    private val _appConfigurationRepository: IAppConfigurationRepository by inject()
    private val _logger: ILogger by inject()


    private var _binding: FInvoiceDetailHeaderBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FInvoiceDetailHeaderBinding.inflate(inflater, container, false)
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
        val appConfiguration = _appConfigurationRepository.get()
        _dateFormatter = Helpers.getDateFormatter(appConfiguration)

        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)

        binding.chkShowCustomerName.isChecked = _currentInvoiceEditor.invoice.customer!!.showCustomerName
        binding.chkShowInvoiceCode.isChecked = _currentInvoiceEditor.invoice.showInvoiceCode
        binding.txtInvoiceCode.setText(_currentInvoiceEditor.invoice.code)
        binding.chkShowInvoiceCreationDate.isChecked = _currentInvoiceEditor.invoice.showInvoiceCreationDate
        binding.chkShowInvoiceExpirationDate.isChecked =
            _currentInvoiceEditor.invoice.showInvoiceExpirationDate

        if (_currentInvoiceEditor.invoice.creationDate != null)
        {
            binding.txtInvoiceCreationDate.setText(_dateFormatter.format(_currentInvoiceEditor.invoice.creationDate!!))
        }

        if (_currentInvoiceEditor.invoice.expirationDate != null)
        {
            binding.txtInvoiceExpirationDate.setText(_dateFormatter.format(_currentInvoiceEditor.invoice.expirationDate!!))
        }

        binding.chkShowYourCompanyDetails.isChecked =
            _currentInvoiceEditor.invoice.company!!.showCompanyDetails
        binding.txtCompanyName.setText(_currentInvoiceEditor.invoice.company!!.name)
        binding.txtCompanyHolderName.setText(_currentInvoiceEditor.invoice.company!!.holderName)
        binding.txtVATNumber.setText(_currentInvoiceEditor.invoice.company!!.vatNumber)
        binding.txtAddress1.setText(_currentInvoiceEditor.invoice.company!!.address1)

        if (!_currentInvoiceEditor.invoice.company!!.phone1.isNullOrBlank())
        {
            binding.txtPhone1.setText(
                PhoneNumberUtils.formatNumber(
                    _currentInvoiceEditor.invoice.company!!.phone1,
                    Locale.getDefault().country))
        }

        if (!_currentInvoiceEditor.invoice.company!!.phone2.isNullOrBlank())
        {
            binding.txtPhone2.setText(
                PhoneNumberUtils.formatNumber(
                    _currentInvoiceEditor.invoice.company!!.phone2,
                    Locale.getDefault().country))
        }

        binding.txtEmail1.setText(_currentInvoiceEditor.invoice.company!!.email1)
        binding.txtWebsite.setText(_currentInvoiceEditor.invoice.company!!.webSite)
        binding.chkShowYourCompanyLogo.isChecked = _currentInvoiceEditor.invoice.company!!.showCompanyLogo
    }

    private fun back()
    {
        try
        {
            _currentInvoiceEditor.invoice.customer!!.showCustomerName =
                binding.chkShowCustomerName.isChecked
            _currentInvoiceEditor.invoice.showInvoiceCode = binding.chkShowInvoiceCode.isChecked
            _currentInvoiceEditor.invoice.code = binding.txtInvoiceCode.text()
            _currentInvoiceEditor.invoice.showInvoiceCreationDate =
                binding.chkShowInvoiceCreationDate.isChecked
            _currentInvoiceEditor.invoice.showInvoiceExpirationDate =
                binding.chkShowInvoiceExpirationDate.isChecked

            if (binding.txtInvoiceExpirationDate.text().isNotBlank())
            {
                _currentInvoiceEditor.invoice.expirationDate =
                    _dateFormatter.parse(binding.txtInvoiceExpirationDate.text())
            }

            _currentInvoiceEditor.invoice.company!!.showCompanyDetails =
                binding.chkShowYourCompanyDetails.isChecked
            _currentInvoiceEditor.invoice.company!!.name = binding.txtCompanyName.text()
            _currentInvoiceEditor.invoice.company!!.holderName = binding.txtCompanyHolderName.text()
            _currentInvoiceEditor.invoice.company!!.vatNumber = binding.txtVATNumber.text()
            _currentInvoiceEditor.invoice.company!!.address1 = binding.txtAddress1.text()
            _currentInvoiceEditor.invoice.company!!.phone1 = binding.txtPhone1.text()
            _currentInvoiceEditor.invoice.company!!.phone2 = binding.txtPhone2.text()
            _currentInvoiceEditor.invoice.company!!.email1 = binding.txtEmail1.text()
            _currentInvoiceEditor.invoice.company!!.webSite = binding.txtWebsite.text()
            _currentInvoiceEditor.invoice.company!!.showCompanyLogo =
                binding.chkShowYourCompanyLogo.isChecked
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
        binding.navigationTopBar.btnBack.setOnClickListener {
            back()
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.navigationTopBar.btnBack.isEnabled)
            {
                back()
            }
        }
        callback.isEnabled = true

        _calendar = Calendar.getInstance()

        val dateSetForInvoiceCreationDateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                _calendar.set(Calendar.YEAR, year)
                _calendar.set(Calendar.MONTH, month)
                _calendar.set(Calendar.DAY_OF_MONTH, day)
                binding.txtInvoiceCreationDate.setText(_dateFormatter.format(_calendar.time))
            }

        binding.txtInvoiceCreationDate.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetForInvoiceCreationDateListener,
                _calendar.get(Calendar.YEAR),
                _calendar.get(Calendar.MONTH),
                _calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dateSetForInvoiceExpirationDateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                _calendar.set(Calendar.YEAR, year)
                _calendar.set(Calendar.MONTH, month)
                _calendar.set(Calendar.DAY_OF_MONTH, day)
                binding.txtInvoiceExpirationDate.setText(_dateFormatter.format(_calendar.time))
            }

        binding.txtInvoiceExpirationDate.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetForInvoiceExpirationDateListener,
                _calendar.get(Calendar.YEAR),
                _calendar.get(Calendar.MONTH),
                _calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.txtEmail1.doOnTextChanged {

            if (binding.txtEmail1.text().isNotEmpty())
            {
                if (Helpers.isValidEmail(binding.txtEmail1.text()))
                {
                    binding.txtEmail1.clearErrors()
                    binding.navigationTopBar.btnBack.isEnabled = true
                }
                else
                {
                    binding.txtEmail1.setError(
                        getString(R.string.validation_invalid_email_address))
                    binding.navigationTopBar.btnBack.isEnabled = false
                }
            }
            else
            {
                binding.txtEmail1.clearErrors()
                binding.navigationTopBar.btnBack.isEnabled = true
            }

        }
    }

}