package com.francescobonizzi.invoiceEstimateReceiptMaker.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.AdArticleBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Article
import java.text.NumberFormat


/** Template item della RecycleView */
class ArticleAdapter(
    private val _articles: MutableList<Article>,
    private val _numberFormat: NumberFormat
) :
    RecyclerView.Adapter<ArticleAdapter.MyViewHolder>()
{
    class MyViewHolder(binding: AdArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val txtName = binding.txtArticleName
        val txtSingleItemPrice = binding.txtArticleSingleItemPrice
        val txtArticleDescription = binding.txtArticleDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdArticleBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val currentArticle = _articles[position]
        holder.txtName.text = currentArticle.name
        holder.txtSingleItemPrice.text = _numberFormat.format(currentArticle.singleItemPrice)
        holder.txtArticleDescription.text = currentArticle.description
    }

    override fun getItemCount(): Int
    {
        return _articles.size
    }
}
