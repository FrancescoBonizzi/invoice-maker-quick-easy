package com.francescobonizzi.invoiceEstimateReceiptMaker.utilities

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R

/** Utilizzato per gestire il click degli oggetti delle RecycleView */
class ItemClickSupport private constructor(private val recyclerView: RecyclerView)
{
    companion object
    {
        fun addTo(view: RecyclerView): ItemClickSupport
        {
            var support: ItemClickSupport? =
                view.getTag(R.id.item_click_support) as? ItemClickSupport
            if (support == null)
            {
                support = ItemClickSupport(view)
            }
            return support
        }

    }

    private var onItemClick: ((Int) -> Unit)? = null

    private val onClickListener = View.OnClickListener { v ->
        val holder = recyclerView.getChildViewHolder(v)
        onItemClick?.invoke(holder.adapterPosition)
    }

    private val attachListener = object : RecyclerView.OnChildAttachStateChangeListener
    {
        override fun onChildViewAttachedToWindow(view: View)
        {
            if (onItemClick != null)
            {
                view.setOnClickListener(onClickListener)
            }
        }

        override fun onChildViewDetachedFromWindow(view: View)
        {
        }
    }

    init
    {
        recyclerView.setTag(R.id.item_click_support, this)
        recyclerView.addOnChildAttachStateChangeListener(attachListener)
    }

    fun onItemClick(onItemClick: (Int) -> Unit): ItemClickSupport
    {
        this.onItemClick = onItemClick
        return this
    }

}
