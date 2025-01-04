package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.francescobonizzi.invoiceEstimateReceiptMaker.R

class SettingsActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_settings)

        // Nascondo la support bar perché è brutta
        supportActionBar?.hide()
    }
}