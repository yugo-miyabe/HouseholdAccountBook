package com.yuni.myportforlio.drawerUI

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuni.myportforlio.CustomRecyclerViewAdapter
import com.yuni.myportforlio.R
import com.yuni.myportforlio.WalletDB
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private lateinit var adapter: CustomRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "履歴"
        realm = Realm.getDefaultInstance()

        val realmResults = realm.where(WalletDB::class.java)
            .findAll()
            .sort("id", Sort.DESCENDING)

        layoutManager = LinearLayoutManager(null)
        historyView.layoutManager = layoutManager

        adapter = CustomRecyclerViewAdapter(realmResults)
        historyView.adapter = this.adapter

    }

    /**
     * 戻るボタンの処理
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
