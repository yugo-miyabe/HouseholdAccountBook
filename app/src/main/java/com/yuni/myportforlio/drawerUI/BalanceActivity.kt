package com.yuni.myportforlio.drawerUI

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.yuni.myportforlio.R
import com.yuni.myportforlio.WalletDB
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_balance.*
import java.text.SimpleDateFormat
import java.util.*

class BalanceActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    //"yyyy/MM/dd"のフォーマットを取得
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd")
    //"yy/MM"のフォーマットを取得
    private val lineDate = SimpleDateFormat("yy/MM")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance)
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()

        var monthSpring :Long = 0
        var monthIncome :Long = 0
        var sumSpring :Long = 0
        var sumIncome :Long = 0

        //カレンダークラスを取得
        val calendar = Calendar.getInstance()
        calendar.clear()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.balanceTitle)

        //現在の月の何日目かを取得
        val monthNum = calendar.get(Calendar.DAY_OF_MONTH)
        //月の最初を取得
        val monthFirst : Date = Calendar.getInstance().run {
            add(Calendar.DATE, - monthNum + 1)
            calendar.time  //getTime()
        }
        //月の最初をカレンダークラスにセット
        calendar.time = monthFirst
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DATE, - 1)
        val monthEnd : Date = Calendar.getInstance().run {
            calendar.time    //getTime()
        }
        val realmResults = realm.where<WalletDB>()
            .findAll()
        //Realmのモデルを取得
        val rResults: RealmResults<WalletDB> = realmResults
        //件数を取得
        val realmMax = rResults.size - 1

        for (realmCount in 0..realmMax) {
            val realmDate : Date = rResults[realmCount]!!.date
            //支出取得
            if (!rResults[realmCount]!!.flg) {
                if (dateFormat.format(realmDate) in dateFormat.format(monthFirst)..dateFormat.format(monthEnd)) {
                    monthSpring += rResults[realmCount]?.money.toString().toLong()
                }
                sumSpring += rResults[realmCount]?.money.toString().toLong()
            }
            //収入
            if (rResults[realmCount]!!.flg) {
                if (dateFormat.format(realmDate) in dateFormat.format(monthFirst)..dateFormat.format(monthEnd)) {
                    monthIncome += rResults[realmCount]?.money.toString().toLong()
                }
                sumIncome += rResults[realmCount]?.money.toString().toLong()
            }
        }
        val balanceMoneyText = sumIncome - sumSpring
        val balanceMoneyString = getString(R.string.yenMark) + balanceMoneyText

        balanceMoney.text = balanceMoneyString

        //グラフの作成
        createLine()

    }

    /**
     * 過去12か月の月初を取得
     */
    private fun createLine(){
        val realmResults = realm.where<WalletDB>()
            .findAll()
        //Realmのモデルを取得
        val rResults: RealmResults<WalletDB> = realmResults

        //現在月から12月前までを配列
        val monthFirstArray = Array(12){ Date() }
        val monthEndArray = Array(12){ Date() }
        val monthBalanceMoneyArray = Array(12) {it * 0L}

        //カレンダークラスを取得
        val calendar = Calendar.getInstance()
        //現在の月の何日目かを取得
        val monthNum = calendar.get(Calendar.DAY_OF_MONTH)

        //月の最初を取得
        val monthFirstDay : Date = Calendar.getInstance().run {
            add(Calendar.DATE, - monthNum + 1)
            time  //getTime()
        }
        //月の最初をカレンダークラスにセット
        calendar.time = monthFirstDay
        val realmMax =  rResults.size - 1
        val max = 12
        for(i in 0 until max) {
            calendar.time = Date()
            val workFirst = Calendar.getInstance().run {
                calendar.add(Calendar.DATE, - monthNum + 1)
                calendar.add(Calendar.MONTH,  - i)
                calendar.time    //getTime()
            }
            monthFirstArray[i] = workFirst
            calendar.time = monthFirstDay
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.DATE, - 1)
            //月の最後を取得
            val workEnd : Date = Calendar.getInstance().run {
                calendar.add(Calendar.MONTH, 1 - i)
                calendar.time    //getTime()
            }
            monthEndArray[i] = workEnd
        }

        for(i in 0 until max) {
            var spendingMoney  = 0L
            var incomeMoney = 0L
            for (realmCount in 0..realmMax) {
                val realmDate : Date = rResults[realmCount]!!.date
                //if (dateFormat.format(realmDate) in dateFormat.format(monthFirstArray[i])..dateFormat.format(monthEndArray[i])) {

                if (dateFormat.format(realmDate) in dateFormat.format(monthFirstArray[i])..dateFormat.format(monthEndArray[i])  && rResults[realmCount]?.flg == false) {
                    spendingMoney += rResults[realmCount]?.money.toString().toLong()
                }
                if (dateFormat.format(realmDate) in dateFormat.format(monthFirstArray[i])..dateFormat.format(monthEndArray[i]) && rResults[realmCount]?.flg == true) {
                    incomeMoney += rResults[realmCount]?.money.toString().toLong()
                }
            }
            if(i > 0) {
                monthBalanceMoneyArray[i] = monthBalanceMoneyArray[i - 1]
                monthBalanceMoneyArray[i] = incomeMoney - spendingMoney
            }else  {
                monthBalanceMoneyArray[i] = incomeMoney - spendingMoney
            }
        }
        setupLineChart(monthFirstArray.reversedArray())
        balanceLineChart.data = lineData(monthBalanceMoneyArray.reversedArray())

    }
    // LineChart用のデータ作成
    private fun lineData(moneyArray:Array<Long>): LineData {
        val values = mutableListOf<Entry>()
        val itemText = "残高推移"

        for (i in 0 until 12) {
            val value =moneyArray[i].toFloat()
            values.add(Entry(i.toFloat(), value))
        }

        // グラフのレイアウトの設定
        val yVals = LineDataSet(values, itemText).apply {
            axisDependency = YAxis.AxisDependency.LEFT

            color = Color.BLUE
            // タップ時のハイライトカラー
            highLightColor = Color.YELLOW

            setDrawCircles(true)
            setDrawCircleHole(true)
            // 点の値非表示
            setDrawValues(false)
            // 線の太さ
            lineWidth = 2f
        }

        return LineData(yVals)
    }

    private fun setupLineChart(monthFirstArray:Array<Date>) {

        val dateArray: Array<String> = Array(12) { lineDate.format(monthFirstArray[it]).toString() }
        val lineChart = balanceLineChart

        lineChart.apply {
            description.isEnabled = false
            //タッチを有効に設定
            setTouchEnabled(true)
            //ドラック有効
            isDragEnabled = false
            // 拡大縮小可能
            isScaleXEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)
        }
        //データラベルの表示
        lineChart.legend.apply {
            form = Legend.LegendForm.LINE
            //typeface = mTypeface
            textSize = 9f
            textColor = Color.BLACK
            //データラベルの位置(上)
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            //データラベルの位置(右)
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            //データラベルの位置(水平)
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
        }

        //y軸右側の設定
        lineChart.axisRight.isEnabled = false

        //X軸表示
        lineChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(dateArray)
            //x軸の凡例の位置
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(true)
            // 格子線を表示する
            setDrawGridLines(true)
            labelCount = 11
            textSize = 9F
        }
        //y軸左側の表示
        lineChart.axisLeft.apply {
            textColor = Color.BLACK
            // 格子線を表示する
            setDrawGridLines(true)
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
        realm.close()
    }

}
