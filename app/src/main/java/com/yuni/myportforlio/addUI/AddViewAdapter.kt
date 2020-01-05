package com.yuni.myportforlio.addUI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuni.myportforlio.R

class AddViewAdapter(private val categoryList:ArrayList<String>,private val listener: ListListener)
    : RecyclerView.Adapter<AddViewHolder>() {

    /**
     * Layoutを設定
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        //LayoutInflaterは、指定したxmlのレイアウト(View)リソースを利用できる
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return AddViewHolder(view)
    }


    /**
     * RecyclerViewで表示するアイテムの個数
     */
    override fun getItemCount(): Int {
        return categoryList.size
    }

    /**
     * Layoutの画像や文字を設定する
     */
    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        holder.titleView.text = categoryList[position]
        holder.itemView.setOnClickListener {
            listener.onClickRow(it, categoryList[position])
        }
    }

    interface ListListener {
        fun onClickRow(tappedView: View, category: String)
    }
}