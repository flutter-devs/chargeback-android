package com.example.subscriptions_app.view.component


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.subscriptions_app.repository.impl.InMemorySubscriptionRepository
import com.example.subscriptions_app.viewmodel.EditSubscriptionViewModel

object EditViewModelFactoryProvider {
    fun provide(): ViewModelProvider.Factory {
        val repo = InMemorySubscriptionRepository()
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(EditSubscriptionViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return EditSubscriptionViewModel(repo) as T
                }
                throw IllegalArgumentException("Unknown VM")
            }
        }
    }
}
