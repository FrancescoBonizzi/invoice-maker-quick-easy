package com.francescobonizzi.invoiceEstimateReceiptMaker.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.francescobonizzi.invoiceEstimateReceiptMaker.R

class QuotationsFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.f_quotations, container, false)
    }
}