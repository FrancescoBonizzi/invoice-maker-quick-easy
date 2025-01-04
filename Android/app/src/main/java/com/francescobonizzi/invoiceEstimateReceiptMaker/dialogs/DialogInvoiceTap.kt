package com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.DInvoiceTapBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice
import com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail.InvoiceDetailActivity
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IInvoicesRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogInvoiceTap(
    private val _selectedInvoice: Invoice,
    private val _selectedInvoiceAdapter: RecyclerView.Adapter<*>,
    private val _allInvoices: MutableList<Invoice>,
    private val _selectedInvoicePosition: Int,
    private val _invoicesRepository: IInvoicesRepository,
    private val _onItemDeleted: () -> Unit
) : BottomSheetDialogFragment()
{
    private var _binding: DInvoiceTapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = DInvoiceTapBinding.inflate(inflater, container, false)
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
        binding.txtDialogTitle.text = _selectedInvoice.code
    }

    private fun setHandlers()
    {
        binding.btnCancelDialog.setOnClickListener {
            dismiss()
        }

        binding.btnEditInvoice.setOnClickListener {
            val intent = Intent(requireContext(), InvoiceDetailActivity::class.java)
            intent.putExtra("invoiceId", _allInvoices.indexOf(_selectedInvoice))
            startActivity(intent)

            dismiss()
        }

        binding.btnDeleteInvoice.setOnClickListener {
            _invoicesRepository.delete(_selectedInvoice)
            _selectedInvoiceAdapter.notifyItemRemoved(_selectedInvoicePosition)
            _onItemDeleted()
            dismiss()
        }

    }

}