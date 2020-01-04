package com.yuni.myportforlio.addUI

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuni.myportforlio.R
import kotlinx.android.synthetic.main.activity_add_recycler.*

class AddRecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recycler)

        // 現在のintentを取得する
        val intent = intent
        // intentから指定キーの文字列を取得する
        val categoryList: ArrayList<String> = intent.getStringArrayListExtra( "categoryListIntent" )

        //戻るボタンの表示
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = category_recycler_list

        val adapter = AddViewAdapter(categoryList,object : AddViewAdapter.ListListener{
            override fun onClickRow(tappedView: View, category: String) {
                this@AddRecyclerActivity.onClickRow(category)
            }
        })

        // RecyclerViewのサイズが固定で決まっているならtrueにするのが良いらしい
        recyclerView.setHasFixedSize(true)
        // RecyclerView内の表示形式にLinearLayoutを指定
        recyclerView.layoutManager = LinearLayoutManager(null)
        // 区切り線の設定
        val divider = DividerItemDecoration(recyclerView.context, LinearLayoutManager(this).orientation)
        // RecyclerViewに区切り線を追加
        recyclerView.addItemDecoration(divider)

        recyclerView.adapter = adapter
    }

    /**
     * 戻るボタンの処理
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    fun onClickRow(category:String) {
        // 最初にキャンセルされた結果をセットしておくことで、端末の戻るボタンに対応させる
        setResult(Activity.RESULT_CANCELED)
        val result = Intent()
        result.putExtra("categoryListIntent",category)
        setResult(Activity.RESULT_OK,result)
        finish()
    }
}
