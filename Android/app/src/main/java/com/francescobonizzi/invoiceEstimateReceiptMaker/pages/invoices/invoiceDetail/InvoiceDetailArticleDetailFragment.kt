package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FInvoiceDetailArticleDetailBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.InvoiceArticle
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Constants
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.hideKeyboard
import org.koin.android.ext.android.inject
import java.text.NumberFormat

class InvoiceDetailArticleDetailFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _currencyFormat: NumberFormat
    private lateinit var _currentInvoiceEditor: InvoiceEditor

    private var _currentArticle: InvoiceArticle? = null
    private var _somethingChanged: Boolean = false

    private val _appConfigurationRepository: IAppConfigurationRepository by inject()
    private val _logger: ILogger by inject()

    private var _binding: FInvoiceDetailArticleDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        _binding = FInvoiceDetailArticleDetailBinding.inflate(inflater, container, false)
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
            _currentInvoiceEditor =
                (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor()

            // Argument from the caller
            val invoiceArticleId = arguments?.getInt(Constants.InvoiceArticleIdVariable)
            if (invoiceArticleId != null)
            {
                _currentArticle = _currentInvoiceEditor.invoice.articles[invoiceArticleId]
            }

            val appConfiguration = _appConfigurationRepository.get()
            _currencyFormat = Helpers.getCurrencyFormat(appConfiguration)

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

    fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = false

        binding.txtArticlePrice.setPlaceholderText("${getString(R.string.price)} (${_currencyFormat.currency})")

        if (_currentArticle == null)
        {
            binding.txtArticleDetailTitle.text = getString(R.string.new_article_for_invoice)
        }
        else
        {
            // Se è precompilato ho già tutti i valori
            _somethingChanged = true
            binding.txtArticleDetailTitle.text = getString(R.string.invoice_article)
            binding.txtArticleName.setText(_currentArticle!!.name)
            binding.txtArticleDescription.setText(_currentArticle!!.description)
            binding.txtArticlePrice.setText(_currentArticle!!.singleItemPrice.toString())
            binding.txtArticleQuantity.setText(_currentArticle!!.quantity.toString())
            binding.txtArticleTotal.setText(getFormattedTotal())
        }

        refreshSaveButtonState()
    }

    private fun refreshSaveButtonState()
    {
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = !binding.txtArticleName.text().isBlank()
                && !binding.txtArticlePrice.text().isBlank()
                && !binding.txtArticleQuantity.text().isBlank()
                && _somethingChanged
    }

    private fun getFormattedTotal(): String?
    {
        val quantity = binding.txtArticleQuantity.text().toIntOrNull()
        val price = Helpers.textToDoubleOrNull(binding.txtArticlePrice.text())

        if (quantity != null && price != null)
        {
            return _currencyFormat.format(quantity * price)
        }

        return null
    }

    fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            // Se arrivo dalla lista generale degli articoli, questo pop rimanda direttamente a Invoice Article Summary
            // grazie ad una property del navigator
            requireContext().hideKeyboard(it.windowToken)
            _navigationHost.popBackStack()
        }

        binding.txtArticleName.doOnTextChanged {
            _somethingChanged = true
            refreshSaveButtonState()
        }

        binding.txtArticleQuantity.doOnTextChanged {
            _somethingChanged = true
            binding.txtArticleTotal.setText(getFormattedTotal())
            refreshSaveButtonState()
        }

        binding.txtArticlePrice.doOnTextChanged {
            _somethingChanged = true
            binding.txtArticleTotal.setText(getFormattedTotal())
            refreshSaveButtonState()
        }

        binding.txtArticleDescription.doOnTextChanged {
            _somethingChanged = true
            refreshSaveButtonState()
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {

            try
            {
                if (_currentArticle == null)
                {
                    _currentArticle = InvoiceArticle(
                        name = binding.txtArticleName.text(),
                        description = binding.txtArticleDescription.text(),
                        singleItemPrice = Helpers.textToDouble(binding.txtArticlePrice.text()),
                        quantity = binding.txtArticleQuantity.text().toInt()
                    )

                    _currentInvoiceEditor.invoice.articles.add(_currentArticle!!)
                }
                else
                {
                    _currentArticle!!.name = binding.txtArticleName.text()
                    _currentArticle!!.description = binding.txtArticleDescription.text()
                    _currentArticle!!.singleItemPrice = Helpers.textToDouble(binding.txtArticlePrice.text())
                    _currentArticle!!.quantity = binding.txtArticleQuantity.text().toInt()
                }
            }
            catch (exception: Exception)
            {
                _logger.error(exception)
                DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_save))
            }

            requireContext().hideKeyboard(it.windowToken)

            // Metto il parametro per i casi in cui arrivo dalla pagina degli articles
            // così quando torno indietro so che è per l'invoice
            val bundle = Bundle()
            bundle.putBoolean(Constants.IsForInvoiceBundleVariable, true)
            _navigationHost.popBackStack()
        }

    }
}


