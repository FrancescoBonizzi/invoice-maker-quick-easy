package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail

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
import com.francescobonizzi.invoiceEstimateReceiptMaker.adapters.InvoiceArticleAdapter
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FInvoiceDetailArticlesSummaryBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs.DialogInvoiceArticleTap
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.InvoiceArticle
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IArticlesRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Constants
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.ItemClickSupport
import org.koin.android.ext.android.inject
import java.text.NumberFormat

class InvoiceDetailArticlesSummaryFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _viewAdapter: RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager
    private lateinit var _currencyFormat: NumberFormat

    private lateinit var _articles: MutableList<InvoiceArticle>
    private lateinit var _currentInvoiceEditor: InvoiceEditor

    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()
    private val _articlesRepository: IArticlesRepository by inject()

    private var _binding: FInvoiceDetailArticlesSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FInvoiceDetailArticlesSummaryBinding.inflate(inflater, container, false)
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
            _navigationHost = requireView().findNavController()
            _currentInvoiceEditor =
                (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor()

            val appConfiguration = _appConfigurationRepository.get()
            _currencyFormat = Helpers.getCurrencyFormat(appConfiguration)

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

    private fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.navigationTopBar.btnAdditionalTopRightButton.text = ""
        binding.navigationTopBar.btnAdditionalTopRightButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add, 0)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE

        _articles = _currentInvoiceEditor.invoice.articles
        _viewManager = LinearLayoutManager(requireContext())
        _viewAdapter = InvoiceArticleAdapter(_articles, _currencyFormat)

        _recyclerView = binding.listProducts.apply {
            layoutManager = _viewManager
            adapter = _viewAdapter
        }

        binding.txtTotalWithoutTaxes.text = _currencyFormat.format(
            _currentInvoiceEditor.invoice.articles.sumByDouble { it.quantity * it.singleItemPrice })
    }

    private fun setHandlers()
    {
        ItemClickSupport.addTo(_recyclerView).onItemClick { position ->

            val dialog = DialogInvoiceArticleTap(
                _articles[position],
                _viewAdapter,
                position,
                _currentInvoiceEditor.invoice,
                _navigationHost)
            dialog.show(parentFragmentManager, tag)

        }

        binding.navigationTopBar.btnBack.setOnClickListener {
            _navigationHost.popBackStack()
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {
            // Se ho almeno un articolo salvato in anagrafica generale, vado all'elenco per velocizzare la selezione
            if (_articlesRepository.getAll().isNotEmpty())
            {
                val bundle = Bundle()
                bundle.putBoolean(Constants.IsForInvoiceBundleVariable, true)
                _navigationHost.navigate(
                    R.id.action_invoiceDetailArticlesSummaryFragment_to_articlesFragment,
                    bundle)
            }
            else
            {
                _navigationHost.navigate(R.id.action_invoiceDetailArticlesSummaryFragment_to_invoiceDetailArticleDetail)
            }

        }
    }

}