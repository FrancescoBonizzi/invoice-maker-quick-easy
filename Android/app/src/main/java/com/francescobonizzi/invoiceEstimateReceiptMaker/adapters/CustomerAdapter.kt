package com.francescobonizzi.invoiceEstimateReceiptMaker.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.AdCustomerBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Customer
import java.util.*


/** Template item della RecycleView */
class CustomerAdapter(
    private val _customers: MutableList<Customer>,
) :
    RecyclerView.Adapter<CustomerAdapter.MyViewHolder>()
{

    class MyViewHolder(binding: AdCustomerBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        val txtCustomerName: TextView = binding.txtCustomerName
        val txtCustomerPhone: TextView = binding.txtCustomerPhone
        val txtCustomerIcon: TextView = binding.txtCustomerIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdCustomerBinding.inflate(inflater, parent, false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val currentCustomer = _customers[position]
        holder.txtCustomerName.text = currentCustomer.name
        holder.txtCustomerPhone.text = currentCustomer.phone
        holder.txtCustomerIcon.text = getCustomerInitials(currentCustomer.name)

        (holder.txtCustomerIcon.background as GradientDrawable)
            .setColor(Color.parseColor(getCustomerPersonalColor(currentCustomer.name)))
    }

    override fun getItemCount(): Int
    {
        return _customers.size
    }

    private fun getCustomerPersonalColor(customerName: String?): String
    {
        if (customerName.isNullOrBlank())
            return "#000000"

        return String.format("#%06X", (0xFFFFFF and customerName.hashCode()))
    }

    private fun getCustomerInitials(customerName: String?): String
    {
        if (customerName.isNullOrBlank())
            return ""

        try
        {
            var initials = ""
            val splitted = customerName.toUpperCase(Locale.ROOT).split(" ")

            if (splitted.count() == 0)
            {
                initials += customerName.toUpperCase(Locale.ROOT)[0]
            }
            else if (splitted.count() >= 1)
            {
                initials += splitted[0][0]
            }

            if (splitted.count() >= 2)
            {
                initials += splitted[1][0]
            }
            return initials
        }
        catch (exception: Exception)
        {
            return ""
        }
    }

}
