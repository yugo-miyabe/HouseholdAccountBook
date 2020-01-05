package com.yuni.myportforlio.addUI

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.yuni.myportforlio.R
import com.yuni.myportforlio.WalletDB
import com.yuni.myportforlio.databinding.ActivityAddBinding
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_add.*
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private var flgSpIn: Boolean = false     //false支出
    private val compositeDisposable = CompositeDisposable()
    val properties = AddProperties()
    private val PICK_CONTACT_REQUEST = 1001  // The request code
    private var categoryList: ArrayList<String> = getSpendingCategoryList()
    private var bpId :Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        realm = Realm.getDefaultInstance()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.addTitle)

        val binding  = DataBindingUtil.setContentView<ActivityAddBinding>(this, R.layout.activity_add)
        binding.activityAdd = this@AddActivity

        compositeDisposable.add(properties.getValidationObservable())

        //ボタンの色設定
        spendingButton.setBackgroundColor(Color.rgb(204,0,204)) //ピンク
        incomeButton.setBackgroundColor(Color.rgb(192,192,192)) //グレー

        // 修正できるように start
        bpId = intent.getLongExtra("id",0L)
        if (bpId > 0L ) {
            //修正の場合
            val realmDB = realm.where<WalletDB>()
                .equalTo("id",bpId).findFirst()
            dateEditText.text = realmDB!!.date.toString()
            memoEditText.setText(realmDB.memo)
            moneyEditText.setText(realmDB.money.toString())
            deleteButton.visibility = View.VISIBLE
            incomeButton.visibility = View.INVISIBLE
            spendingButton.visibility = View.INVISIBLE
        } else {
            //追加の場合
            deleteButton.visibility = View.INVISIBLE
        }
    }

    /**
     * 日付項目クリック
     */
    fun onCategoryEditText(){
        val intent = Intent(this, AddRecyclerActivity::class.java)
        intent.putExtra("categoryListIntent", categoryList)
        startActivityForResult(intent,PICK_CONTACT_REQUEST)
    }

    /**
     * 支払ボタンクリック
     */
    fun onSpendingButton(){
        flgSpIn = false
        spendingButton.setBackgroundColor(Color.rgb(204,0,204)) //ピンク
        incomeButton.setBackgroundColor(Color.rgb(192,192,192)) //グレー
        categoryList = getSpendingCategoryList()
    }

    /**
     * 収入ボタンクリック
     */
    fun onIncomeButton(){
        flgSpIn = true
        incomeButton.setBackgroundColor(Color.rgb(204,0,204)) //ピンク
        spendingButton.setBackgroundColor(Color.rgb(192,192,192)) //グレー
        categoryList = getIncomeCategoryList()
    }

    /**
     * 日付項目クリック
     */
    fun onDateClicked(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DATE]
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, y, m, d ->
                properties.dateOfUse = Calendar.getInstance().
                    apply { set(y, m, d) } },
            year, month, day)
            .show()
    }

    /**
     * 保存ボタン
     */
    fun onRegisterClicked(){
        var date: Date? = dateEditText.text.toString().toDate("yyyy/MM/dd")
        var category = ""           //項目
        var memo = ""               //内容
        var money: Long = 0         //金額

        if (!categoryEditText.text.isNullOrEmpty()){
            category = categoryEditText.text.toString()
        }
        if (!dateEditText.text.isNullOrEmpty()) {
            date = dateEditText.text.toString().toDate("yyyy/MM/dd")
        }
        if (!memoEditText.text.isNullOrEmpty()) {
            memo = memoEditText.text.toString()
        }
        if (!moneyEditText.text.isNullOrEmpty()) {
            money = moneyEditText.text.toString().toLong()
        }
        // 修正できるように start
        when (bpId) {
            0L -> { // 修正できるように end
                realm.executeTransaction {
                    val maxId = realm.where<WalletDB>().max("id")
                    val nextId = (maxId?.toLong() ?: 0L) + 1L
                    //createObject()メソッド・・・モデルのインスタンスを作成
                    //モデルクラス<WalletDB>とプライマリーキー(nextId)の値を指定
                    val walletDB = realm.createObject<WalletDB>(nextId)
                    //各プロパティの値をセット
                    walletDB.date = Date()
                    if (date is Date){
                        walletDB.date = date
                    }
                    walletDB.category = category
                    walletDB.memo = memo
                    walletDB.money = money
                    walletDB.flg = flgSpIn
                }
            }
            else -> {
                realm.executeTransaction{
                    val walletDB = realm.where<WalletDB>()
                        .equalTo("id",bpId).findFirst()
                    //各プロパティの値をセット
                    walletDB?.date = Date()
                    if (date is Date){
                        walletDB?.date = date
                    }
                    walletDB?.category = category
                    walletDB?.memo = memo
                    walletDB?.money = money
                    walletDB?.flg = flgSpIn
                }
            }
        }
        Toast.makeText(applicationContext, getText(R.string.savedButton), Toast.LENGTH_SHORT).show()
        finish()
    }

    /**
     * 削除ボタン
     */
    fun onDeleteClicked(){
        realm.executeTransaction {
            realm.where<WalletDB>()
                .equalTo("id",bpId)
                ?.findFirst()
                ?.deleteFromRealm()
        }
        Toast.makeText(applicationContext, getText(R.string.deletedButton), Toast.LENGTH_SHORT).show()
        finish()
    }

    /**
     * 支払項目
     */
    private fun getSpendingCategoryList(): ArrayList<String> {
        return arrayListOf(
            "食費", "日用雑貨", "交通費", "交際費", "教育・教養", "美容.衣類", "その他"
        )
    }

    /**
     * 収入項目
     */
    private  fun getIncomeCategoryList(): ArrayList<String> {
        return  arrayListOf(
            "給与所得","賞与","臨時収入","その他"
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PICK_CONTACT_REQUEST){ return }
        if (resultCode == Activity.RESULT_OK && data != null){
            val message = data.getStringExtra("categoryListIntent")
            categoryEditText.text = message
        } else if(resultCode == Activity.RESULT_CANCELED){
            categoryEditText.text = ""
        }
    }

    private fun String.toDate(pattern: String = "yyyy/MM/dd"): Date? {
        return try {
            SimpleDateFormat(pattern).parse(this)
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: ParseException) {
            return null
        }
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
        compositeDisposable.dispose()
        realm.close()
    }
}


