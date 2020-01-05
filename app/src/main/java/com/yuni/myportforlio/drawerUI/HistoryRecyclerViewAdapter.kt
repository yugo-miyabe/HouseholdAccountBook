package com.yuni.myportforlio.drawerUI

import android.content.Intent
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import android.text.format.DateFormat
import android.view.LayoutInflater.*
import com.yuni.myportforlio.R
import com.yuni.myportforlio.ViewHolder
import com.yuni.myportforlio.WalletDB
import com.yuni.myportforlio.addUI.AddActivity

//Realmのクエリの実行結果であるRealmResultsを受け取る
class HistoryRecyclerViewAdapter(realmResults: RealmResults<WalletDB>) : RecyclerView.Adapter<ViewHolder>() {
    private val rResults: RealmResults<WalletDB> = realmResults

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        //履歴画面
        val view = from(parent.context)
            .inflate(R.layout.history_result, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rResults.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val walletDB = rResults[position]
        //履歴画面処理
        holder.dateText?.text = DateFormat.format("yyyy/MM/dd", walletDB?.date)
        holder.itemText?.text = walletDB?.category.toString()
        holder.remarks?.text = walletDB?.memo.toString()
        holder.money?.text = "￥" + walletDB?.money.toString()
        holder.itemView.setBackgroundColor(if (position % 2 == 0) Color.LTGRAY else Color.WHITE)

        // 修正・削除できるように start
        holder.itemView.setOnClickListener{
            val intent = Intent(it.context, AddActivity::class.java)
            intent.putExtra("id",walletDB?.id)
            it.context.startActivity(intent)
        }
        // 修正・削除できるように end
    }
}