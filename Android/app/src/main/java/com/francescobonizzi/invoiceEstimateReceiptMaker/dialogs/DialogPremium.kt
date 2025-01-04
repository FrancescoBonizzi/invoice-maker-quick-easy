package com.francescobonizzi.invoiceEstimateReceiptMaker.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.android.billingclient.api.SkuDetails
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.DPremiumBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.BillingHandler
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class DialogPremium(private val _activity: Activity) : BottomSheetDialogFragment()
{
    private val _logger: ILogger by inject()
    private lateinit var _billingHandler: BillingHandler
    private lateinit var _unlimitedInvoicesSku: SkuDetails

    private var _binding: DPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = DPremiumBinding.inflate(inflater, container, false)
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
                0
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
            DialogsHelpers.showError(requireActivity(), getString(R.string.playstore_error))
            dismiss()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        _billingHandler.destroy()
    }

    @SuppressLint("SetTextI18n")
    private fun setPageData()
    {
        binding.btnCancelDialog.visibility = View.INVISIBLE
        binding.btnGetPremium.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE

        _billingHandler = BillingHandler(
            _activity = requireActivity(),
            _onError = {
                _logger.error(it)
                DialogsHelpers.showError(requireActivity(), getString(R.string.playstore_error))
                dismiss()
            },
            _onAlreadyPurchasedUnlimitedInvoicesLoaded = {
                binding.progressBar.visibility = View.INVISIBLE
                // Lo risetto per sicurezza qualora fossi arrivato qui senza aver ancora impostato il parametro localmente
                BillingHandler.setUnlimitedInvoicesPurchased()
                setPurchasedData()
            },
            _onNotPurchasedUnlimitedInvoicesLoaded = {
                BillingHandler.setUnlimitedInvoicesNOTPurchased()
                _unlimitedInvoicesSku = it
                binding.btnGetPremium.text = "${getString(R.string.unlock_premium)} ${it.price}"
                binding.btnCancelDialog.visibility = View.VISIBLE
                binding.btnGetPremium.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            },
            _onPurchasedItem = {
                BillingHandler.setUnlimitedInvoicesPurchased()
                DialogsHelpers.showConfirmation(_activity, getString(R.string.thanks_for_purchase))
                dismiss()
            },
            _onPurchasedItemCancel = {
                dismiss()
            }
        )
    }

    private fun setPurchasedData()
    {
        binding.txtMainText.text = getString(R.string.your_are_premium)
        binding.txtPro.visibility = View.GONE
        binding.txtUpgrade.visibility = View.GONE
        binding.btnGetPremium.visibility = View.GONE
    }

    private fun setHandlers()
    {
        binding.btnCancelDialog.setOnClickListener {
            dismiss()
        }

        binding.btnGetPremium.setOnClickListener {
            binding.btnCancelDialog.visibility = View.INVISIBLE
            binding.btnGetPremium.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            _billingHandler.buyUnlimitedInvoices(_unlimitedInvoicesSku)
        }
    }


}