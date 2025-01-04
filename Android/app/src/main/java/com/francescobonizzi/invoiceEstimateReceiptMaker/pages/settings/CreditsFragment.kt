package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsCreditsBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers

class CreditsFragment : Fragment()
{
    private lateinit var _navigationHost: NavController
    private var _binding: FSettingsCreditsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        _binding = FSettingsCreditsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        _navigationHost = requireView().findNavController()
        setHandlers()
        setPageData()
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener { _navigationHost.popBackStack() }
    }

    private fun setPageData()
    {
        binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)
        binding.txtCreditsContent.text = HtmlCompat.fromHtml(getString(R.string.credits_content), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}