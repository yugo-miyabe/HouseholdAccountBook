package com.yuni.myportforlio.addUI

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuni.myportforlio.R

class AddRecyclerViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    val titleView: TextView = itemView.findViewById(R.id.row_title)
}