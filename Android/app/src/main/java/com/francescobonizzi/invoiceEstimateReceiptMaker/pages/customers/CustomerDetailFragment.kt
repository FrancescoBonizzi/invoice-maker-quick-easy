package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.customers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FCustomerDetailBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Customer
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.ICustomersRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.*
import org.koin.android.ext.android.inject


class CustomerDetailFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private var _currentCustomer: Customer? = null
    private var _somethingChanged: Boolean = false

    private val _customersRepository: ICustomersRepository by inject()
    private val _logger: ILogger by inject()

    private var _binding: FCustomerDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FCustomerDetailBinding.inflate(inflater, container, false)
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
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarBackgroundColor)

        try
        {
            // Argument from the caller
            val customerId = arguments?.getInt("customerId")
            if (customerId != null)
            {
                _currentCustomer = _customersRepository.get(customerId)
            }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != IntentHelpers.IntentSelectContactCode
            || resultCode != Activity.RESULT_OK
            || data == null
            || data.data == null) return

        try
        {
            val contactUri = data.data!!

            val contactContentResolver = requireContext().contentResolver
            val contactId = ContactsHelper.getContactId(contactContentResolver, contactUri) ?: return

            binding.txtCustomerName.setText(ContactsHelper.getContactName(contactContentResolver, contactUri))
            binding.txtCustomerPhone.setText(ContactsHelper.getContactPhone(contactContentResolver, contactId))
            binding.txtCustomerEmail.setText(ContactsHelper.getContactEmail(contactContentResolver, contactId))
            binding.txtCustomerAddress.setText(ContactsHelper.getAddress(contactContentResolver, contactId))
        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error))
            _navigationHost.popBackStack()
        }
    }

    fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = false

        if (_currentCustomer == null)
        {
           binding.txtCustomerDetailTitle.text = getString(R.string.new_customer)
        }
        else
        {
            binding.txtCustomerDetailTitle.text = getString(R.string.customer_detail)
            binding.txtCustomerName.setText(_currentCustomer!!.name)
            binding.txtCustomerAddress.setText(_currentCustomer!!.address)
            binding.txtCustomerEmail.setText(_currentCustomer!!.email)
            binding.txtCustomerPhone.setText(_currentCustomer!!.phone)
        }
    }

    private fun refreshSaveButtonState()
    {
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled =
            !binding.txtCustomerName.text().isBlank() && _somethingChanged
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((grantResults.isNotEmpty()
                    && requestCode == IntentHelpers.PermissionSelectContacts
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED))
        {
            IntentHelpers.startIntentToSelectContact(this)
        }
    }

    fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            requireContext().hideKeyboard(it.windowToken)
            _navigationHost.popBackStack()
        }

        binding.txtCustomerName.doOnTextChanged {
            _somethingChanged = true
            refreshSaveButtonState()
        }

        binding.txtCustomerPhone.doOnTextChanged {
            _somethingChanged = true
            refreshSaveButtonState()
        }

        binding.txtCustomerAddress.doOnTextChanged {
            _somethingChanged = true
            refreshSaveButtonState()
        }

        binding.txtCustomerEmail.doOnTextChanged {
            _somethingChanged = true
            refreshSaveButtonState()
        }

        binding.btnImportFromContacts.setOnClickListener {
            IntentHelpers.startIntentToSelectContact(this)
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {

            try
            {
                if (_currentCustomer == null)
                {
                    _currentCustomer = Customer(
                        name = binding.txtCustomerName.text(),
                        address = binding.txtCustomerAddress.text(),
                        email = binding.txtCustomerEmail.text(),
                        phone = binding.txtCustomerPhone.text()
                    )

                    _customersRepository.add(_currentCustomer!!)
                }
                else
                {
                    _currentCustomer!!.name = binding.txtCustomerName.text()
                    _currentCustomer!!.address = binding.txtCustomerAddress.text()
                    _currentCustomer!!.email = binding.txtCustomerEmail.text()
                    _currentCustomer!!.phone = binding.txtCustomerPhone.text()
                }

                _customersRepository.save()
            }
            catch (exception: Exception)
            {
                _logger.error(exception)
                DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_save))
            }

            requireContext().hideKeyboard(it.windowToken)
            _navigationHost.popBackStack()
        }
    }
}