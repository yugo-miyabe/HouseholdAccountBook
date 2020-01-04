package com.yuni.myportforlio

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import io.realm.Realm
import io.realm.RealmConfiguration

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)                //Realmライブラリの初期化
        val config = RealmConfiguration.Builder().build()

        // 全件削除
        Realm.deleteRealm(config)
        Realm.setDefaultConfiguration(config)
        AndroidThreeTen.init(this)  //ThreeTenABPの初期化
    }

}