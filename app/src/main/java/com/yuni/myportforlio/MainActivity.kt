package com.yuni.myportforlio

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.firebase.auth.FirebaseAuth
import com.yuni.myportforlio.addUI.AddActivity
import com.yuni.myportforlio.drawerUI.AnalysisActivity
import com.yuni.myportforlio.drawerUI.BalanceActivity
import com.yuni.myportforlio.drawerUI.HistoryActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{

    private lateinit var realm: Realm
    private var toDayMoney : Long = 0
    private var toWeekMoney : Long = 0
    private var toMonthMoney : Long = 0
    //"yyyy/MM/dd"のフォーマットを取得
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd")
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            /*
            if (auth.currentUser!!.isEmailVerified) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
            */
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)


        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(realmConfiguration)

        realm = Realm.getDefaultInstance()


        //収支追加
        val fab: FloatingActionButton = this.findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        //現在の日付を取得
        val toDay = Date()
        //カレンダークラスを取得
        val calendar = Calendar.getInstance()
        //曜日の取得 日～土:1～7
        val weekNum = calendar.get(Calendar.DAY_OF_WEEK)
        //現在の月の何日目かを取得
        val monthNum = calendar.get(Calendar.DAY_OF_MONTH)

        //週の最初を取得
        val weekFirst : Date = Calendar.getInstance().run {
            add(Calendar.DATE, - weekNum + 1)
            time  //getTime()
        }
        //月の最初を取得
        val monthFirst : Date = Calendar.getInstance().run {
            add(Calendar.DATE, - monthNum + 1)
            time  //getTime()
        }
        //週の最初をカレンダークラスにセット
        calendar.time = weekFirst
        //週の最後を取得
        val weekEnd : Date = Calendar.getInstance().run{
            add(Calendar.DATE, + 6 )
            time  //getTime()
        }
        //月の最初をカレンダークラスにセット
        calendar.time = monthFirst
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DATE, - 1)
        //月の最後を取得
        val monthEnd : Date = Calendar.getInstance().run {
            calendar.time    //getTime()
        }
        val realmResults = realm.where<WalletDB>()
            .findAll()
        //Realmのモデルを取得
        val rResults: RealmResults<WalletDB> = realmResults
        val realmMax = rResults.size - 1
        //今月分を項目別に格納
        val spendingArray: Array<String> = arrayOf(
            "食費",
            "日用雑貨",
            "交通費",
            "交際費",
            "教育・教養",
            "美容.衣類",
            "その他"
        )

        val spendingItemMoney:Array<Long> = Array(7) {0L}
        val spendingItemMoneyText:Array<String> = Array(7) {""}
        val realmDate=Array(1000){ Date() }
        val realmItem =Array(1000){""}
        val realmMoney:Array<Long> = Array(1000){0L}  //Long型の最大値
        for (realmCount in 0..realmMax) {
            realmDate[realmCount] = rResults[realmCount]!!.date
            realmItem[realmCount] = rResults[realmCount]!!.category
            realmMoney[realmCount] = rResults[realmCount]!!.money
        }

        for (realmCount in 0..realmMax) {
            if (!rResults[realmCount]!!.flg) {
                if (dateFormat.format(realmDate[realmCount]) == dateFormat.format(toDay)) {
                    toDayMoney += realmMoney[realmCount]
                }
                if (dateFormat.format(realmDate[realmCount]) in dateFormat.format(weekFirst)..dateFormat.format(weekEnd)) {
                    toWeekMoney += realmMoney[realmCount]
                }
                if (dateFormat.format(realmDate[realmCount]) in dateFormat.format(monthFirst)..dateFormat.format(monthEnd)) {
                    toMonthMoney += realmMoney[realmCount]
                }
            }
        }

        val yenMark = getString(R.string.yenMark)

        val toDayMoneyText = yenMark + this.toDayMoney.toString()
        val weekMoneyText =  yenMark + this.toWeekMoney.toString()
        val monthMoneyText =  yenMark + this.toMonthMoney.toString()
        dayMoneyResult.text = toDayMoneyText
        weekMoneyResult?.text = weekMoneyText
        monthMoneyResult?.text = monthMoneyText


        for(itemCount in spendingItemMoney.indices){
            spendingItemMoney[itemCount] = itemExpenditure(spendingArray[itemCount],realmDate,realmItem,realmMoney,monthFirst,monthEnd,realmMax)
        }

        for(itemCount in spendingItemMoney.indices) {
            spendingItemMoneyText[itemCount] = yenMark + spendingItemMoney[itemCount].toString()
        }

        foodExpensesMoney?.text = spendingItemMoneyText[0]
        dailyGoodsMoney?.text = spendingItemMoneyText[1]
        transportationCostsMoney?.text = spendingItemMoneyText[2]
        datingExpensesMoney?.text = spendingItemMoneyText[3]
        EducationCultivationMoney?.text = spendingItemMoneyText[4]
        BeautyClothingMoney?.text = spendingItemMoneyText[5]
        OtherMoney?.text = spendingItemMoneyText[6]
    }

    private fun itemExpenditure(spendingItem :String, realmDate:Array<Date>, realmItem:Array<String>, realmMoney:Array<Long>, monthFirst: Date, monthEnd: Date, realmMax:Int):Long{
        var itemSum: Long = 0
        for (cnt in 0..realmMax){
            if (spendingItem == realmItem[cnt] && dateFormat.format(realmDate[cnt]) in dateFormat.format(monthFirst)..dateFormat.format(monthEnd)){
                itemSum += realmMoney[cnt]
            }
        }
        return itemSum
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home -> {
                //画面遷移をしない
            }
            R.id.nav_history -> {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            }
            R.id.nav_analysis -> {
                startActivity(Intent(this@MainActivity, AnalysisActivity::class.java))
            }
            R.id.nav_balance -> {
                startActivity(Intent(this@MainActivity,BalanceActivity::class.java))
            }
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
