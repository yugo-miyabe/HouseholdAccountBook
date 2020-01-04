package com.yuni.myportforlio

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.history_result.view.*

class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    var dateText: TextView? = null
    var itemText: TextView? = null
    var remarks: TextView? = null
    var money: TextView? = null

    init {
        //ビューホルダーのプロパティとレイアウトのViewの対応
        dateText = itemView.dateResult
        itemText = itemView.itemResult
        remarks = itemView.remarksResult
        money = itemView.moneyResult
    }
}