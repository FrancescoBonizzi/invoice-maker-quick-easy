package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FArticleDetailBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsCreditsBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Article
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IArticlesRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Constants
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.hideKeyboard
import org.koin.android.ext.android.inject
import java.text.NumberFormat

class ArticleDetailFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private lateinit var _currencyFormat: NumberFormat
    private var _currentArticle: Article? = null
    private var _somethingChanged: Boolean = false

    private val _appConfigurationRepository: IAppConfigurationRepository by inject()
    private val _articlesRepository: IArticlesRepository by inject()
    private val _logger: ILogger by inject()

    private var _binding: FArticleDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FArticleDetailBinding.inflate(inflater, container, false)
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
            val articleId = arguments?.getInt(Constants.ArticleIdVariable)
            if (articleId != null)
            {
                _currentArticle = _articlesRepository.get(articleId)
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

    private fun refreshSaveButtonState()
    {
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = !binding.txtArticleName.text().isBlank()
                && !binding.txtArticlePrice.text().isBlank()
                && _somethingChanged
    }

    fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE
        binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = false

        binding.txtArticlePrice.setPlaceholderText("${getString(R.string.price)} (${_currencyFormat.currency})")

        if (_currentArticle == null)
        {
            binding.txtArticleDetailTitle.text = getString(R.string.new_article)
        }
        else
        {
            binding.txtArticleDetailTitle.text = getString(R.string.article_detail)
            binding.txtArticleName.setText(_currentArticle!!.name)
            binding.txtArticleDescription.setText(_currentArticle!!.description)
            binding.txtArticlePrice.setText(_currentArticle!!.singleItemPrice.toString())
        }

        refreshSaveButtonState()
    }

    fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {
            requireContext().hideKeyboard(it.windowToken)
            _navigationHost.popBackStack()
        }

        binding.txtArticleName.doOnTextChanged {
            _somethingChanged = true
            refreshSaveButtonState()
        }

        binding.txtArticlePrice.doOnTextChanged {
            _somethingChanged = true
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
                    _currentArticle = Article(
                        name = binding.txtArticleName.text(),
                        description = binding.txtArticleDescription.text(),
                        singleItemPrice = Helpers.textToDouble(binding.txtArticlePrice.text())
                    )

                    _articlesRepository.add(_currentArticle!!)
                }
                else
                {
                    _currentArticle!!.name = binding.txtArticleName.text()
                    _currentArticle!!.description = binding.txtArticleDescription.text()
                    _currentArticle!!.singleItemPrice = Helpers.textToDouble(binding.txtArticlePrice.text())
                }

                _articlesRepository.save()
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