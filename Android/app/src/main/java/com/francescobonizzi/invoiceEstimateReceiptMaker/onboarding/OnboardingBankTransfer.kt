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
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.DOnboardingBankTransferBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class OnboardingBankTransfer : BottomSheetDialogFragment()
{
    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: DOnboardingBankTransferBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = DOnboardingBankTransferBinding.inflate(inflater, container, false)
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
        binding.txtHolderName.setText(appConfiguration.payments.checkHolder)
        binding.txtBankDetails.setText(appConfiguration.payments.bankDetails)
    }

    private fun refreshButtonState()
    {
        binding.btnNext.isEnabled = !binding.txtHolderName.text.isNullOrBlank() && !binding.txtBankDetails.text.isNullOrBlank()
    }

    private fun setHandlers()
    {
        binding.txtHolderName.doOnTextChanged { _, _, _, _ ->
            refreshButtonState()
        }

        binding.txtBankDetails.doOnTextChanged { _, _, _, _ ->
            refreshButtonState()
        }

        binding.btnNext.setOnClickListener {

            if (!binding.txtHolderName.text.isNullOrBlank() && !binding.txtBankDetails.text.isNullOrBlank())
            {
                val appConfiguration = _appConfigurationRepository.get()
                appConfiguration.payments.checkHolder = binding.txtHolderName.text.toString()
                appConfiguration.payments.bankDetails = binding.txtBankDetails.text.toString()
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