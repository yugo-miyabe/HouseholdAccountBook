package com.yuni.myportforlio.addUI

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuni.myportforlio.R

class AddViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    val titleView: TextView = itemView.findViewById(R.id.row_title)
    //val detailView: TextView = itemView.findViewById(R.id.row_detail)
}