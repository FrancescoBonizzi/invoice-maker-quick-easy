package com.francescobonizzi.invoiceEstimateReceiptMaker.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.francescobonizzi.invoiceEstimateReceiptMaker.R

class OnboardingFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(
            R.layout.f_onboarding,
            container,
            false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // Il primo step è il Welcome
        val welcomeDialog = OnboardingWelcome()
        welcomeDialog.show(parentFragmentManager, tag)
    }


}