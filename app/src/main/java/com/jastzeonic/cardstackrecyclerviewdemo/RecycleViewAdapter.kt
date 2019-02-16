package com.jastzeonic.cardstackrecyclerviewdemo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.jastzeonic.cardstackrecyclerviewdemo.model.ImageModel
import kotlinx.android.synthetic.main.image_view_item.view.*

class RecycleViewAdapter(var items: MutableList<ImageModel>) :
        RecyclerView.Adapter<ItemViewHolder>() {


    override fun getItemCount(): Int {
        return if (items.size > 0) {
            30
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.view.image_view.setImageResource(items[position % items.size].resId)
        holder.view.text_view.text = items[position % items.size].name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.image_view_item, parent, false)
        )
    }

}