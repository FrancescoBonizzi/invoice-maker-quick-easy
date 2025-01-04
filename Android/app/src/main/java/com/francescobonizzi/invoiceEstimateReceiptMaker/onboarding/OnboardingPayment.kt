package com.francescobonizzi.invoiceEstimateReceiptMaker.onboarding

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.DOnboardingPaymentBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class OnboardingPayment : BottomSheetDialogFragment()
{
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()

    private var _binding: DOnboardingPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = DOnboardingPaymentBinding.inflate(inflater, container, false)
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

        setHandlers()
    }

    private fun setHandlers()
    {
        binding.btnPayPal.setOnClickListener {
            dismiss()
            val onboardingPaypal = OnboardingPaypal()
            onboardingPaypal.show(parentFragmentManager, tag)
        }

        binding.btnBankTransfer.setOnClickListener {
            dismiss()
            val onboardingBankTransfer = OnboardingBankTransfer()
            onboardingBankTransfer.show(parentFragmentManager, tag)
        }

        binding.btnCash.setOnClickListener {
            dismiss()
            _appConfigurationRepository.get().payments.showCash = true
            _appConfigurationRepository.save()
            val onboardingCompleted = OnboardingCompleted()
            onboardingCompleted.show(parentFragmentManager, tag)

        }

        binding.btnCreditCard.setOnClickListener {
            dismiss()
            _appConfigurationRepository.get().payments.showCreditCard = true
            _appConfigurationRepository.save()
            val onboardingCompleted = OnboardingCompleted()
            onboardingCompleted.show(parentFragmentManager, tag)
        }

        binding.btnDebitCard.setOnClickListener {
            dismiss()
            _appConfigurationRepository.get().payments.showDebitCard = true
            _appConfigurationRepository.save()
            val onboardingCompleted = OnboardingCompleted()
            onboardingCompleted.show(parentFragmentManager, tag)
        }

        binding.btnSkip.setOnClickListener {
            dismiss()
            val onboardingCompleted = OnboardingCompleted()
            onboardingCompleted.show(parentFragmentManager, tag)
        }
    }
}
