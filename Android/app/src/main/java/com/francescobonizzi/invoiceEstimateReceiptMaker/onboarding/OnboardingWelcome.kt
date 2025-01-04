package com.francescobonizzi.invoiceEstimateReceiptMaker.onboarding

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.DOnboardingWelcomeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OnboardingWelcome : BottomSheetDialogFragment()
{
    private var _binding: DOnboardingWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = DOnboardingWelcomeBinding.inflate(inflater, container, false)
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

        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED
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
       binding.btnNext.setOnClickListener {
            dismiss()
            val onboardingCompanyNameDialog = OnboardingCompanyName()
            onboardingCompanyNameDialog.show(parentFragmentManager, tag)
        }
    }
}
