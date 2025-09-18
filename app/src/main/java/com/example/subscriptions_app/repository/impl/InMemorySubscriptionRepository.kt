package com.example.subscriptions_app.repository.impl


import android.os.Build
import androidx.annotation.RequiresApi
import com.example.subscriptions_app.R
import com.example.subscriptions_app.model.*
import com.example.subscriptions_app.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class InMemorySubscriptionRepository : SubscriptionRepository {
    private val store = MutableStateFlow<List<Subscription>>(initialSeed())

    override fun getSubscription(id: String): Flow<Subscription?> =
        store.map { list -> list.find { it.id == id } }

    override suspend fun saveSubscription(subscription: Subscription) {
        val list = store.value.toMutableList()
        val idx = list.indexOfFirst { it.id == subscription.id }
        if (idx >= 0) list[idx] = subscription else list.add(subscription)
        store.value = list
    }

    override suspend fun deleteSubscription(id: String) {
        store.value = store.value.filterNot { it.id == id }
    }

    override suspend fun getApps(): List<App> {
        return listOf(
            App("Netflix", R.drawable.ic_netflix),
            App("Spotify", R.drawable.ic_sportify),
            App("New York Times", R.drawable.ic_new_yourk),
            App("Wall Street Journal", R.drawable.ic_wall),
            App("Hulu", R.drawable.ic_hulu),
            App("Apple", R.drawable.ic_apple),
            App("Amazon", R.drawable.ic_amazon)
        )
    }

    override suspend fun getCategories(): List<App> {
        return listOf(
            App("Subscription", R.drawable.ic_subscription),
            App("Utility", R.drawable.ic_utility),
            App("Card Payment", R.drawable.ic_card),
            App("Loan", R.drawable.ic_loan),
            App("Rent", R.drawable.ic_home)
        )
    }

    override suspend fun getFrequencies(): List<App> {
        return listOf(
            App("Weekly", -1),
            App("Monthly", -1),
            App("Anually", -1),
            )
    }

    override suspend fun remindDaysBefore(): List<App> {
        return listOf(
            App("1 days before", -1),
            App("2 days before", -1),
            App("3 days before", -1),
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initialSeed(): List<Subscription> {
        val app = App("Netflix", R.drawable.ic_netflix)
        return listOf(
            Subscription(
                app = app,
                amount = 50.0,
                category = App("Subscription",  R.drawable.ic_subscription),
                startDate = LocalDate.now(),
                frequency =App("Weekly",-1),
                remindDaysBefore= App("2 days before",-1),
                active = true
            )
        )
    }
}
