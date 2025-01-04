package com.francescobonizzi.invoiceEstimateReceiptMaker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import java.text.SimpleDateFormat
import java.util.*

/** Template item dello spinner */
class DateFormatAdapter(
    context: Context,
    resource: Int,
    private var items: List<String>
) : ArrayAdapter<String>(context, resource, items)
{

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItem(position: Int): String
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

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View
    {

        var v: View? = convertView

        if (v == null)
        {
            val inflater = LayoutInflater.from(context)
            v = inflater.inflate(R.layout.ad_spinner_item_date_format, null)
        }

        val txtDateFormat =
            v!!.findViewById<View>(R.id.spinner_item_date_format_txtDateFormat) as TextView
        txtDateFormat.text = items[position]

        val txtDateExample =
            v.findViewById<View>(R.id.spinner_item_date_format_txtDateExample) as TextView
        txtDateExample.text = "Ex: ${SimpleDateFormat(items[position]).format(Date())}"

        return v
    }

}