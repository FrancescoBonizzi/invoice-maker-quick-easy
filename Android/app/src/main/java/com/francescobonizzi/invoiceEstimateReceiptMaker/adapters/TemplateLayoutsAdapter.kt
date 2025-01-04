package com.francescobonizzi.invoiceEstimateReceiptMaker.adapters

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.AdTemplateLayoutBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.TemplateLayout
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers

/** Template item della RecycleView che rappresenta i layout delle fatture */
class TemplateLayoutsAdapter(
    private val _templateLayouts: Array<TemplateLayout>,
    private val _assetsManager: AssetManager) :
    RecyclerView.Adapter<TemplateLayoutsAdapter.MyViewHolder>()
{
    var selectedPosition = -1

    class MyViewHolder(binding: AdTemplateLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)
    {
        val txtLayoutName: TextView = binding.txtLayoutName
        val imgLayoutPlacholder: ImageView = binding.imgLayoutPlaceholder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdTemplateLayoutBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val currentLayout = _templateLayouts[position]
        holder.txtLayoutName.text = currentLayout.name

        _assetsManager.open(currentLayout.imagePath).use {
            holder.imgLayoutPlacholder.setImageBitmap(Helpers.getBitmap(it))
        }

        if (selectedPosition == position)
        {
            holder.txtLayoutName.typeface = Typeface.DEFAULT_BOLD
            holder.txtLayoutName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            holder.imgLayoutPlacholder.setBackgroundResource(R.drawable.template_layout_choice_border)
        }
        else
        {
            holder.txtLayoutName.typeface = Typeface.DEFAULT
            holder.txtLayoutName.setCompoundDrawables(null, null, null, null)
            holder.imgLayoutPlacholder.setBackgroundResource(0)
        }
    }

    override fun getItemCount(): Int
    {
        return _templateLayouts.size
    }
}
