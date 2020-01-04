package com.yuni.myportforlio

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class WalletDB : RealmObject(){
    @PrimaryKey
    var id: Long = 0
    var flg: Boolean = false        //false支払い true収入
    var date: Date = Date()
    var category: String = ""
    var memo: String = ""
    var money: Long = 0
}