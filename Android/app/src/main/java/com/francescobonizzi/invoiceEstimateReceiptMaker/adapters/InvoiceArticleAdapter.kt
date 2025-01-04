package com.francescobonizzi.invoiceEstimateReceiptMaker.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.AdInvoiceArticleBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.InvoiceArticle
import java.text.NumberFormat

/** Template item della RecycleView */
class InvoiceArticleAdapter(
    private val _articles: MutableList<InvoiceArticle>,
    private val _numberFormat: NumberFormat
) :
    RecyclerView.Adapter<InvoiceArticleAdapter.MyViewHolder>()
{

    class MyViewHolder(binding: AdInvoiceArticleBinding)
        : RecyclerView.ViewHolder(binding.root)
    {
        val txtName: TextView = binding.txtProductName
        val txtSingleItemPrice: TextView = binding.txtProductSingleItemPrice
        val txtArticleQuantity: TextView = binding.txtArticleQuantity
        val txtArticlePriceWithQuantity: TextView = binding.txtArticlePriceWithQuantity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdInvoiceArticleBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val currentProduct = _articles[position]
        holder.txtName.text = currentProduct.name
        holder.txtSingleItemPrice.text = _numberFormat.format(currentProduct.singleItemPrice)
        holder.txtArticleQuantity.text = "x${currentProduct.quantity}"
        holder.txtArticlePriceWithQuantity.text =
            _numberFormat.format(currentProduct.singleItemPrice * currentProduct.quantity)
    }

    override fun getItemCount(): Int
    {
        return _articles.size
    }


}
