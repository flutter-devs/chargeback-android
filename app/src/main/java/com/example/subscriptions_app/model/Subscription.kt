package com.example.subscriptions_app.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.subscriptions_app.R
import java.util.*
import java.time.LocalDate

data class Subscription @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String = UUID.randomUUID().toString(),
    var app: App = App("Unknown", R.drawable.ic_netflix),
    var amount: Double = 0.0,
    var category: App = App("Subscription", R.drawable.ic_subscription),
    var startDate: LocalDate = LocalDate.now(),
    var frequency: App = App("Weekly",-1),
    var remindDaysBefore:App= App("2 days before",-1),
    var active: Boolean = true
)
