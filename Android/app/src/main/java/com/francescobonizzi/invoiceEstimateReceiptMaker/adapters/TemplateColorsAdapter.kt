package com.francescobonizzi.invoiceEstimateReceiptMaker.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.AdTemplateColorBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.TemplateColor


/** Template item della RecycleView che rappresenta i colori dei layout delle fatture */
class TemplateColorsAdapter(
    private val _templateColors: Array<TemplateColor>) :
    RecyclerView.Adapter<TemplateColorsAdapter.MyViewHolder>()
{
    var selectedPosition = -1

    class MyViewHolder(binding: AdTemplateColorBinding)
        : RecyclerView.ViewHolder(binding.root)
    {
        val imgIsChoosen: ImageView = binding.imgIsChoosen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdTemplateColorBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val currentColor = _templateColors[position]
        (holder.imgIsChoosen.background as GradientDrawable).setColor(Color.parseColor(currentColor.primaryColor))

        if (selectedPosition == position)
            holder.imgIsChoosen.setImageResource(R.drawable.ic_baseline_check_24)
        else
            holder.imgIsChoosen.setImageDrawable(null)
    }

    override fun getItemCount(): Int
    {
        return _templateColors.size
    }
}
