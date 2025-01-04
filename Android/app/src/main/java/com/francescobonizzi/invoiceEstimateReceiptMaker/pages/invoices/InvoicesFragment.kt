package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices

import android.content.Intent
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
import com.francescobonizzi.invoiceEstimateReceiptMaker.adapters.InvoiceAdapter
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FInvoicesBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs.DialogInvoiceTap
import com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs.DialogPremium
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.BillingHandler
import com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail.InvoiceDetailActivity
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IInvoicesRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.ItemClickSupport
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat

class InvoicesFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _viewAdapter: RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager
    private lateinit var _invoices: MutableList<Invoice>
    private lateinit var _dateFormatter: SimpleDateFormat

    private val _logger: ILogger by inject()
    private val _invoicesRepository: IInvoicesRepository by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: FInvoicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FInvoicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume()
    {
        super.onResume()
        // Per gestire quando torni qui dall'activity di edit fattura
        setPageData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarBackgroundColorForItemsActivity)

        try
        {
            _navigationHost = requireView().findNavController()

            val appConfiguration = _appConfigurationRepository.get()
            _dateFormatter = Helpers.getDateFormatter(appConfiguration)

            setPageData() // Per primo perché qui inizializzo la RecyclerView
            setHandlers()
        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_load))
            _navigationHost.popBackStack()
        }
    }

    private fun evaluateEmptyPlaceholder()
    {
        if (_invoices.isEmpty())
        {
            binding.containerNoItems.visibility = View.VISIBLE
        }
        else
        {
            // Devo metterlo anche se il fragment parte già con questo nascosto perché posso tornare qui da un'activity
            // quando quindi la pagina era già aperta
            binding.containerNoItems.visibility = View.GONE
        }
    }

    private fun setPageData()
    {
        binding.navigationTopBar.btnBack.visibility = View.INVISIBLE
        binding.navigationTopBar.btnAdditionalTopRightButton.text = ""
        binding.navigationTopBar.btnAdditionalTopRightButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add, 0)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE

        _invoices = _invoicesRepository.getAll()
        evaluateEmptyPlaceholder()

        _viewManager = LinearLayoutManager(requireContext())
        _viewAdapter = InvoiceAdapter(_invoices, _dateFormatter, requireContext())

        _recyclerView = binding.listInvoices.apply {
            layoutManager = _viewManager
            adapter = _viewAdapter
        }
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {

            if (!BillingHandler.canCreateNewInvoices(_invoicesRepository))
            {
                val dialog = DialogPremium(requireActivity())
                dialog.show(parentFragmentManager, tag)
            }
            else
            {
                // Creazione nuova invoice
                val intent = Intent(requireContext(), InvoiceDetailActivity::class.java)
                startActivity(intent)
            }

        }

        ItemClickSupport.addTo(_recyclerView).onItemClick { position ->
            val dialog = DialogInvoiceTap(
                _selectedInvoice = _invoices[position],
                _selectedInvoiceAdapter = _viewAdapter,
                _allInvoices = _invoices,
                _selectedInvoicePosition = position,
                _invoicesRepository = _invoicesRepository,
                _onItemDeleted = { evaluateEmptyPlaceholder() })
            dialog.show(parentFragmentManager, tag)
        }
    }

}