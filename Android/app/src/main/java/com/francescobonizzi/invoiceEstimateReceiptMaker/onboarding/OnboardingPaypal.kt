package com.francescobonizzi.invoiceEstimateReceiptMaker.onboarding

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.doOnTextChanged
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.DOnboardingPaypalBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class OnboardingPaypal : BottomSheetDialogFragment()
{
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: DOnboardingPaypalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = DOnboardingPaypalBinding.inflate(inflater, container, false)
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

        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
        dialog?.setCancelable(false)

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
                resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
            )
            parent.layoutParams = layoutParams
        }

        try
        {
            setPageData()
            setHandlers()
        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_load))
        }
    }

    private fun setPageData()
    {
        binding.btnNext.isEnabled = false
        val appConfiguration = _appConfigurationRepository.get()
        binding.txtPayPalEmail.setText(appConfiguration.payments.payPalEmail)
    }

    private fun setHandlers()
    {
        binding.txtPayPalEmail.doOnTextChanged { _, _, _, _ ->
            binding.btnNext.isEnabled = !binding.txtPayPalEmail.text.isNullOrEmpty()
        }

        binding.btnNext.setOnClickListener {
            if (!binding.txtPayPalEmail.text.isNullOrEmpty())
            {
                val appConfiguration = _appConfigurationRepository.get()
                appConfiguration.payments.payPalEmail = binding.txtPayPalEmail.text.toString()
                _appConfigurationRepository.save()

                dismiss()
                val onboardingCompleted = OnboardingCompleted()
                onboardingCompleted.show(parentFragmentManager, tag)
            }
        }

        binding.btnBack.setOnClickListener {
            dismiss()
            val onboardingPayment = OnboardingPayment()
            onboardingPayment.show(parentFragmentManager, tag)
        }
    }
}