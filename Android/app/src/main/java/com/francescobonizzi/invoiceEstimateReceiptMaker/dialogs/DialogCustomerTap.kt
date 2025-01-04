package com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.DCustomerTapBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Customer
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.ICustomersRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogCustomerTap(
    private val _selectedCustomer: Customer,
    private val _selectedCustomerAdapter: RecyclerView.Adapter<*>,
    private val _allCustomers: MutableList<Customer>,
    private val _selectedCustomerPosition: Int,
    private val _navigationHost: NavController,
    private val _customersRepository: ICustomersRepository,
    private val _onItemDeleted: () -> Unit
) : BottomSheetDialogFragment()
{
    private var _binding: DCustomerTapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = DCustomerTapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)

        // Per mettere il margine laterale, a farlo direttamente dalla view non funziona
        val resources = resources
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            val parent = view?.parent as View
            val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(
                resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin),
                0,
                resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin),
                0
            )
            parent.layoutParams = layoutParams
        }

        setPageData()
        setHandlers()
    }

    private fun setPageData()
    {
        binding.txtDialogTitle.text = _selectedCustomer.name
    }

    private fun setHandlers()
    {
        binding.btnCancelDialog.setOnClickListener {
            dismiss()
        }

        binding.btnEditCustomer.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("customerId", _allCustomers.indexOf(_selectedCustomer))

            _navigationHost.navigate(
                R.id.action_navigationCustomersFragment_to_customerDetailFragment,
                bundle)

            dismiss()
        }

        binding.btnDeleteCustomer.setOnClickListener {
            _customersRepository.delete(_selectedCustomer)
            _selectedCustomerAdapter.notifyItemRemoved(_selectedCustomerPosition)
            _onItemDeleted()
            dismiss()
        }

    }

}