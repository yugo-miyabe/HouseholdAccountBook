package com.yuni.myportforlio.addUI

import androidx.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*

class AddProperties {

    private val moneyTextSubject = BehaviorSubject.create<String>()
    private val dateOfUseSubject = BehaviorSubject.create<Calendar>()
    private val categoryTextSubject = BehaviorSubject.create<String>()

    val dateOfUseValueField: ObservableField<String> = ObservableField()
    val canRegister: ObservableField<Boolean> = ObservableField()

    init {
        moneyTextSubject.onNext("")
        dateOfUseSubject.onNext(Calendar.getInstance())
        categoryTextSubject.onNext("")
    }

    var moneyText :String = ""
        set(value) {
            field = value
            moneyTextSubject.onNext(value)
        }

    var dateOfUse : Calendar? = null
        set(value) {
            field = value
            value?.let {
                dateOfUseValueField.set(SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(value.time))
                dateOfUseSubject.onNext(value)
            }
        }

    var category: String = ""
        set(value) {
            field = value
            categoryTextSubject.onNext(value)
        }


    private val moneyValidationObservable = moneyTextSubject.map {
        it.isNotEmpty()
    }


    private val dateOfUseValidationObservable = dateOfUseSubject.map {
        dateOfUse != null
    }

    private val categoryValidationObservable = categoryTextSubject.map {
        it.isNotEmpty()
    }

    fun getValidationObservable(): Disposable {
        return Observable
            .combineLatest(
                moneyValidationObservable,
                dateOfUseValidationObservable,
                categoryValidationObservable,
                Function3<Boolean,Boolean,Boolean,Boolean>{
                    isValidMoney,isValidDateOfUse,isCategory -> isValidMoney && isValidDateOfUse && isCategory
                })
                .subscribe { canRegister.set(it) }
    }
}

