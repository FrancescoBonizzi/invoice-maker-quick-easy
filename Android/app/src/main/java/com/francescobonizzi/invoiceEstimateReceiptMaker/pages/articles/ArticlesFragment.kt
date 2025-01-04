package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.adapters.ArticleAdapter
import com.francescobonizzi.invoiceEstimateReceiptMaker.customControls.DividerItemDecorator
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FArticlesBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs.DialogArticleTap
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Article
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.InvoiceArticle
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail.InvoiceDetailActivity
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IArticlesRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Constants
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.ItemClickSupport
import org.koin.android.ext.android.inject
import java.text.NumberFormat

class ArticlesFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _viewAdapter: RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager
    private lateinit var _articles: MutableList<Article>
    private lateinit var _currencyFormat: NumberFormat

    private val _logger: ILogger by inject()
    private val _articlesRepository: IArticlesRepository by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _isForInvoice: Boolean = false

    private var _binding: FArticlesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FArticlesBinding.inflate(inflater, container, false)
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
        requireActivity().window.statusBarColor = getColor(requireContext(), R.color.statusBarBackgroundColorForItemsActivity)

        try
        {
            _navigationHost = requireView().findNavController()

            val appConfiguration = _appConfigurationRepository.get()
            _currencyFormat = Helpers.getCurrencyFormat(appConfiguration)

            // Significa che sono arrivato qui per scegliere un article da aggiungere all'invoice
            _isForInvoice = arguments?.getBoolean(Constants.IsForInvoiceBundleVariable) == true

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
        if (_articles.isEmpty())
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

        binding.navigationTopBar.btnAdditionalTopRightButton.text = ""
        binding.navigationTopBar.btnAdditionalTopRightButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add, 0)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE

        _articles = _articlesRepository.getAll()
        evaluateEmptyPlaceholder()

        _viewManager = LinearLayoutManager(requireContext())
        _viewAdapter = ArticleAdapter(_articles, _currencyFormat)

        _recyclerView = binding.listArticles.apply {
            layoutManager = _viewManager
            adapter = _viewAdapter
        }

        val dividerItemDecoration = DividerItemDecorator(getDrawable(requireActivity(), R.drawable.recyclerview_items_divider)!!)
        _recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun setHandlers()
    {
        if (_isForInvoice)
        {
            binding.navigationTopBar.btnBack.setOnClickListener { _navigationHost.popBackStack() }
        }

        // Se utilizzo questa vista per aggiungere gli articoli all'invoice,
        // non permetto la cancellazione o la modifica degli articoli
        if (!_isForInvoice)
        {
            ItemClickSupport.addTo(_recyclerView).onItemClick { position ->
                val dialog = DialogArticleTap(
                    _selectedArticle = _articles[position],
                    _selectedArticleAdapter = _viewAdapter,
                    _allArticles = _articles,
                    _selectedArticlePosition = position,
                    _navigationHost = _navigationHost,
                    _articlesRepository = _articlesRepository,
                    _onItemDeleted = { evaluateEmptyPlaceholder() })
                dialog.show(parentFragmentManager, tag)
            }
        }
        else
        {
            ItemClickSupport.addTo(_recyclerView).onItemClick { position ->

                val selectedArticle = _articles[position]
                val invoice = (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor().invoice
                val newInvoiceArticle = InvoiceArticle(selectedArticle)
                invoice.articles.add(newInvoiceArticle)

                // Rimando alla pagina in cui scegli la quantità dell'articolo
                val bundle = Bundle()
                bundle.putInt(Constants.InvoiceArticleIdVariable, invoice.articles.indexOf(newInvoiceArticle))

                // Se sono qui perché dovevo selezionare un articolo per l'invoice, torno indietro
                _navigationHost.navigate(
                    R.id.action_articlesFragment_to_invoiceDetailArticleDetail,
                    bundle)
            }
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {
            if (!_isForInvoice)
            {
                // Se non è per l'invoice, navigo a dettaglio/creazione di un nuovo articolo per la lista generale
                _navigationHost.navigate(R.id.action_navigationArticlesFragment_to_articleDetailFragment)
            }
            else
            {
                // Se è per l'invoice, navigo a dettaglio/creazione di un nuovo articolo per l'invoice
                _navigationHost.navigate(R.id.action_articlesFragment_to_invoiceDetailArticleDetail)
            }
        }

    }

}