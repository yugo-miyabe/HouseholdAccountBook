package com.yuni.myportforlio.drawerUI

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.yuni.myportforlio.R
import com.yuni.myportforlio.WalletDB
import com.yuni.myportforlio.databinding.ActivityAnalysisBinding
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_analysis.*
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList

class AnalysisActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private var flgSpIn: Boolean = false     //false支出
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()

        val binding = DataBindingUtil.setContentView<ActivityAnalysisBinding>(this,R.layout.activity_analysis)
        binding.activityAnalysis = this@AnalysisActivity
        //カレンダークラスの取得
        val calendar = Calendar.getInstance()
        //テキストの設定
        val thisMonthNumber = calendar.get(Calendar.MONTH) + 1
        val thisMonthText = thisMonthNumber.toString() + "月の出費"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "分析"

        //支出画面の設定
        val thisMonth = findViewById<TextView>(R.id.thisMonth)
        thisMonth.text = thisMonthText

        graphCreate(false)
        //色の設定
        graphSpendingButton.setBackgroundColor(Color.rgb(204, 0, 204)) //ピンク
        graphIncomeButton.setBackgroundColor(Color.rgb(192, 192, 192)) //グレー

    }

    fun onGraphSpendingButtonClick(){
        //カレンダークラスの取得
        val calendar = Calendar.getInstance()
        //テキストの設定
        val thisMonthNumber = calendar.get(Calendar.MONTH) + 1
        flgSpIn = false
        graphCreate(flgSpIn)
        graphSpendingButton.setBackgroundColor(Color.rgb(204,0,204)) //ピンク
        graphIncomeButton.setBackgroundColor(Color.rgb(192,192,192)) //グレー
        val thisMonthTextSpending = thisMonthNumber.toString() + "月の支出"
        thisMonth.text = thisMonthTextSpending
    }

    fun onGraphIncomeButtonClick(){
        //カレンダークラスの取得
        val calendar = Calendar.getInstance()
        //テキストの設定
        val thisMonthNumber = calendar.get(Calendar.MONTH) + 1
        flgSpIn = true
        graphCreate(flgSpIn)
        graphIncomeButton.setBackgroundColor(Color.rgb(204,0,204)) //ピンク
        graphSpendingButton.setBackgroundColor(Color.rgb(192,192,192)) //グレー
        val thisMonthTextIncome = thisMonthNumber.toString() + "月の収入"
        thisMonth.text = thisMonthTextIncome
    }

    private fun graphCreate(flgSpIn:Boolean){

        val item : Array<String>  = Array(100){"0"}
        val money : Array<Long> = Array(100){0L}
        realm = Realm.getDefaultInstance()

        val realmResults = realm.where(WalletDB::class.java)
            .findAll()
        //Realmのモデルを取得
        val rResults: RealmResults<WalletDB> = realmResults
        //件数を取得
        val realmMax = rResults.size - 1

        var arrayMax = 0
        for (realmCount in 0..realmMax) {
            var flg = 0
            for (cnt in 0..arrayMax) {
                if (rResults[realmCount]?.category == item[cnt] && rResults[realmCount]?.flg == flgSpIn) {
                    money[cnt] = rResults[realmCount]?.money.toString().toLong() + money[cnt]
                    flg = 1
                    break
                }
            }
            if(flg == 0 && rResults[realmCount]?.flg == flgSpIn){
                item[arrayMax] = rResults[realmCount]!!.category
                money[arrayMax] = rResults[realmCount]?.money.toString().toLong()
                arrayMax += 1
            }
        }
        arrayMax -= 1

        var sum = 0L

        for (cnt in 0..arrayMax){
            sum += money[cnt]
        }

        val label = "￥$sum"
        totalExpenditureMoney.text = label
        /**
         * 円グラフ作成
         */
        pieCreate(item, money, arrayMax)
        /**
         * 棒グラフ作成
         */
        barCreate(item, money, arrayMax)
    }

    private fun pieCreate(item:Array<String>,money:Array<Long>,arrayMax:Int){

        //val mPie : PieChart = activity!!.findViewById(R.id.pie) as PieChart
        val mPie = findViewById<PieChart>(R.id.pie)

        //パーセント値を使用する設定
        mPie.setUsePercentValues(true)
        mPie.isEnabled = false
        mPie.setDrawEntryLabels(false)      //セクタ内の凡例を削除

        val desc = Description()
        //desc.text = "PieChartのサンプルだよ"
        //mPie.description = desc
        //ラベル非表示
        desc.isEnabled = false


        val legend: Legend? = mPie.legend
        legend?.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT

        val entry = ArrayList<PieEntry>().apply {
            for(cnt in 0..arrayMax) {
                val label =  item[cnt] + "￥" +  money[cnt].toString()
                try {
                    add( PieEntry(money[cnt].toString().toFloat(), label))
                }catch (e: NumberFormatException){
                    add(PieEntry(money[cnt].toString().toFloat(),0f))
                }
            }
        }

        /**
         * ラベル
         */
        val dataSet = PieDataSet(entry, "")
        //グラフのカラーを設定
        dataSet.apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            setDrawValues(false)
            valueTextColor = Color.WHITE        //色
        }

        val pieData = PieData(dataSet)
        pieData.apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(20f)
            setValueTextColor(Color.WHITE)
            setDrawValues(true)        //セクタ内に値表示
        }

        mPie.data = pieData

        //グラフのUI設定
        mPie.apply {
            isRotationEnabled = false
            isDrawHoleEnabled = true         //中心部に穴を開ける
            legend?.isEnabled = true        //凡例の表示
            isClickable = false
            animateY(1200, Easing.EasingOption.Linear)
        }

    }

    private fun barCreate(item:Array<String>,money:Array<Long>,arrayMax:Int){
        //グラフ絵画
        val barChart: BarChart = bar_chart
        //表示データ取得
        barChart.data = BarData(getBarData(money,arrayMax))

        //Y軸(左)の設定
        barChart.axisLeft.apply {
            axisMinimum = 0f
            //axisMaximum = 100f
            labelCount = arrayMax           //表示させるラベル数
            setDrawTopYLabelEntry(true)
            setValueFormatter { value, _ -> "" + value.toInt()}
        }

        //Y軸(右)の設定
        barChart.axisRight.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawZeroLine(false)
            setDrawTopYLabelEntry(true)
        }

        //X軸の設定
        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(item)
            labelCount = arrayMax                //表示させるラベル数
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(true)
            setDrawGridLines(false)
            setDrawAxisLine(true)
        }

        //グラフ上の表示
        barChart.apply {
            setDrawValueAboveBar(true)
            description.isEnabled = false
            isClickable = false
            legend.isEnabled = false //凡例
            setScaleEnabled(false)
            animateY(1200, Easing.EasingOption.Linear)
        }
    }
    /**
     * 棒グラフのデーター設定
     */
    private fun getBarData(money : Array<Long>,arrayMax:Int): ArrayList<IBarDataSet> {
        //表示させるデータ
        val entries: ArrayList<BarEntry> = ArrayList<BarEntry>().apply {
            for (cnt in 0..arrayMax){
                try {
                    add(BarEntry(cnt.toFloat(),money[cnt].toFloat()))
                }catch (e: NumberFormatException){
                    add(BarEntry(money[cnt].toString().toFloat(),0f))
                }
            }
        }

        val dataSet = BarDataSet(entries, "bar").apply {
            //整数で表示
            valueFormatter = IValueFormatter { value, _, _, _ -> "" + value.toInt() }
            //ハイライトさせない
            isHighlightEnabled = false
            //データの色をセット
            colors = ColorTemplate.COLORFUL_COLORS.toList()
        }
        val bars = ArrayList<IBarDataSet>()
        bars.add(dataSet)
        return bars
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