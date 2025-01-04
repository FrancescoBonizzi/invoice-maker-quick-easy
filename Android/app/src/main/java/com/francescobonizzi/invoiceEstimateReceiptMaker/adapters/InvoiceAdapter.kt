package com.francescobonizzi.invoiceEstimateReceiptMaker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.AdInvoiceBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import java.text.SimpleDateFormat
import java.util.*

/** Template item della RecycleView */
class InvoiceAdapter(
    private val _invoices: MutableList<Invoice>,
    private val _dateFormatter: SimpleDateFormat,
    private val _context: Context
) :
    RecyclerView.Adapter<InvoiceAdapter.MyViewHolder>()
{
    class MyViewHolder(binding: AdInvoiceBinding)
        : RecyclerView.ViewHolder(binding.root)
    {
        val txtInvoiceCode: TextView = binding.txtInvoiceCode
        val txtCreationDate: TextView = binding.txtCreationDate
        val txtCustomer: TextView = binding.txtCustomerName
        val txtTotal: TextView = binding.txtInvoiceTotal
        val badgePaid: TextView = binding.badgePaid
        val badgeExpired: TextView = binding.badgeExpired
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdInvoiceBinding.inflate(inflater, parent, false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val currentInvoice = _invoices[position]
        val currentInvoiceEditor = InvoiceEditor(currentInvoice)

        holder.txtInvoiceCode.text = currentInvoice.code
        holder.txtTotal.text = currentInvoiceEditor.getFormattedTotals().formattedTotalAmount
        holder.txtCustomer.text = currentInvoice.customer!!.name
        holder.txtCreationDate.text = _dateFormatter.format(currentInvoice.creationDate!!)

        val shouldHideBadgePaid = currentInvoice.paymentDate == null
        val shouldHideBadgeExpiredDate =
            currentInvoice.expirationDate == null || currentInvoice.expirationDate!! > Date()

        if (!shouldHideBadgePaid)
        {
            holder.badgePaid.visibility = View.VISIBLE
            holder.badgePaid.text = _context.getString(R.string.paid)
        }

        if (!shouldHideBadgeExpiredDate)
        {
            holder.badgeExpired.visibility = View.VISIBLE
            holder.badgeExpired.text = _context.getString(R.string.expired)
        }

        // Faccio questa distinzione perché se almeno uno deve essere visualizzato,
        // 'altro è meglio che sia Gone per non occupare lo spazio laterale
        var invisibilityProperty = View.INVISIBLE
        if (!shouldHideBadgePaid || !shouldHideBadgeExpiredDate)
        {
            invisibilityProperty = View.GONE
        }

        if (shouldHideBadgePaid)
        {
            holder.badgePaid.visibility = invisibilityProperty
        }

        if (shouldHideBadgeExpiredDate)
        {
            holder.badgeExpired.visibility = invisibilityProperty
        }
    }

    override fun getItemCount(): Int
    {
        return _invoices.size
    }
}
