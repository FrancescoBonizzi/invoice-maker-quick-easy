package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.adapters.CustomerAdapter
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FCustomersBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs.DialogCustomerTap
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Customer
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.InvoiceCustomer
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail.InvoiceDetailActivity
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.ICustomersRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Constants
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.ItemClickSupport
import org.koin.android.ext.android.inject

class CustomersFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _viewAdapter: RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager
    private lateinit var _customers: MutableList<Customer>

    private var _isForInvoice: Boolean = false

    private val _logger: ILogger by inject()
    private val _customersRepository: ICustomersRepository by inject()

    private var _binding: FCustomersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FCustomersBinding.inflate(inflater, container, false)
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
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarBackgroundColorForItemsActivity)

        try
        {
            _navigationHost = requireView().findNavController()

            // Significa che sono arrivato qui per scegliere una customer da aggiungere all'invoice
            _isForInvoice = arguments?.getBoolean(Constants.IsForInvoiceBundleVariable) == true

            setPageData() // Per primo perché qui inizializzo la RecyclerView
            setHandlers()
        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_load))
        }
    }

    private fun evaluateEmptyPlaceholder()
    {
        if (_customers.count() == 0)
        {
            binding.containerNoItems.visibility = View.VISIBLE
        }
        else
        {
            binding.containerNoItems.visibility = View.GONE
        }
    }

    private fun setPageData()
    {
        if (_isForInvoice)
        {
            binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        }
        else
        {
            binding.navigationTopBar.btnBack.visibility = View.INVISIBLE
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE
        binding.navigationTopBar.btnAdditionalTopRightButton.text = ""
        binding.navigationTopBar.btnAdditionalTopRightButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add, 0)

        _customers = _customersRepository.getAll()
        evaluateEmptyPlaceholder()

        _viewManager = LinearLayoutManager(requireContext())
        _viewAdapter = CustomerAdapter(_customers)

        _recyclerView = binding.listCustomers.apply {
            layoutManager = _viewManager
            adapter = _viewAdapter
        }

    }

    private fun setHandlers()
    {
        if (_isForInvoice)
        {
            binding.navigationTopBar.btnBack.setOnClickListener { _navigationHost.popBackStack() }
        }

        // Se utilizzo questa vista per aggiungere i customer all'invoice,
        // non permetto la cancellazione o la modifica dei customer
        if (!_isForInvoice)
        {
            ItemClickSupport.addTo(_recyclerView).onItemClick { position ->
                val dialog = DialogCustomerTap(
                    _selectedCustomer = _customers[position],
                    _selectedCustomerAdapter = _viewAdapter,
                    _allCustomers = _customers,
                    _selectedCustomerPosition = position,
                    _navigationHost = _navigationHost,
                    _customersRepository = _customersRepository,
                    _onItemDeleted = {
                        evaluateEmptyPlaceholder()
                    })
                dialog.show(parentFragmentManager, tag)
            }
        }
        else
        {
            // Se vengo da un'invoice, sono qui per ottenere un customer
            ItemClickSupport.addTo(_recyclerView).onItemClick { position ->

                val selectedCustomer = _customers[position]
                val invoice =
                    (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor().invoice

                if (invoice.customer == null)
                {
                    invoice.customer = InvoiceCustomer()
                }

                invoice.customer!!.name = selectedCustomer.name
                invoice.customer!!.address = selectedCustomer.address
                invoice.customer!!.email = selectedCustomer.email
                invoice.customer!!.phone = selectedCustomer.phone

                _navigationHost.popBackStack()
            }

        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {

            if (!_isForInvoice)
            {
                _navigationHost.navigate(R.id.action_navigationCustomersFragment_to_customerDetailFragment)
            }
            else
            {
                _navigationHost.navigate(R.id.action_customersFragment_to_customerDetailFragment2)
            }

        }

    }

}