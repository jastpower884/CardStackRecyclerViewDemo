package com.jastzeonic.cardstackrecyclerviewdemo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view){

    var textView = view.findViewById<TextView>(R.id.text_view)
    var imageView = view.findViewById<ImageView>(R.id.image_view)

}