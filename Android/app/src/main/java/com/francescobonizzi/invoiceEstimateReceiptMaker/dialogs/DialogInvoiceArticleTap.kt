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
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.DInvoiceArticleTapBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.InvoiceArticle
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogInvoiceArticleTap(
    private val _selectedArticle: InvoiceArticle,
    private val _selectedArticleAdapter: RecyclerView.Adapter<*>,
    private val _selectedArticlePosition: Int,
    private val _currentInvoice: Invoice,
    private val _navigationHost: NavController,
) : BottomSheetDialogFragment()
{
    private var _binding: DInvoiceArticleTapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = DInvoiceArticleTapBinding.inflate(inflater, container, false)
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
        binding.txtDialogTitle.text = _selectedArticle.name
    }

    private fun setHandlers()
    {
        binding.btnCancelDialog.setOnClickListener {
            dismiss()
        }

        binding.btnEditArticle.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt(Constants.InvoiceArticleIdVariable, _currentInvoice.articles.indexOf(_selectedArticle))

            _navigationHost.navigate(
                R.id.action_invoiceDetailArticlesSummaryFragment_to_invoiceDetailArticleDetail,
                bundle)

            dismiss()
        }

        binding.btnRemoveArticleFromInvoice.setOnClickListener {
            _currentInvoice.articles.remove(_selectedArticle)
            _selectedArticleAdapter.notifyItemRemoved(_selectedArticlePosition)
            dismiss()
        }
    }

}