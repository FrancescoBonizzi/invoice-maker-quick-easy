package com.francescobonizzi.invoiceEstimateReceiptMaker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import java.util.*

/** Template item dello spinner */
class CurrencyAdapter(
    context: Context,
    resource: Int,
    private var items: List<Currency>
) : ArrayAdapter<Currency>(context, resource, items)
{
    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItem(position: Int): Currency?
    {
        return items[position]
    }

    override fun getCount(): Int
    {
        return items.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        return getDropDownView(position, convertView, parent)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View
    {

        var v: View? = convertView

        if (v == null)
        {
            val inflater = LayoutInflater.from(context)
            v = inflater.inflate(R.layout.ad_spinner_item_currency, null)
        }

        val txtSymbol = v!!.findViewById<View>(R.id.spinner_item_currency_txtSymbol) as TextView
        txtSymbol.text = items[position].currencyCode

        val txtNationality =
            v.findViewById<View>(R.id.spinner_item_currency_txtNationality) as TextView
        txtNationality.text = items[position].displayName

        return v
    }

}