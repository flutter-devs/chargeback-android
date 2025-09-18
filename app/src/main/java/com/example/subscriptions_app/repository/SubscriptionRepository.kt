package com.example.subscriptions_app.repository

import com.example.subscriptions_app.model.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getSubscription(id: String): Flow<Subscription?>
    suspend fun saveSubscription(subscription: Subscription)
    suspend fun deleteSubscription(id: String)
    suspend fun getApps(): List<com.example.subscriptions_app.model.App>
    suspend fun getCategories(): List<com.example.subscriptions_app.model.App>
    suspend fun getFrequencies(): List<com.example.subscriptions_app.model.App>
    suspend fun remindDaysBefore(): List<com.example.subscriptions_app.model.App>
}