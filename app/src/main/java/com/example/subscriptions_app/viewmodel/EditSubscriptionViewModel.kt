package com.example.subscriptions_app.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptions_app.R
import com.example.subscriptions_app.model.App
import com.example.subscriptions_app.model.Category
import com.example.subscriptions_app.model.Frequency
import com.example.subscriptions_app.model.Subscription
import com.example.subscriptions_app.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class Picker { APP, AMOUNT, CATEGORY, FREQUENCY, DATE, REMIND_ME }

data class EditSubscriptionUiState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val subscription: Subscription = Subscription(
        app = App("Netflix", R.drawable.ic_netflix),
        amount = 50.0,
        category = App("Subscription", R.drawable.ic_subscription),
        startDate = LocalDate.now(),
        frequency = App("Weekly",-1),
        remindDaysBefore =  App("2 days before",-1),
        active = true
    ),
    val apps: List<App> = emptyList(),
    val categories: List<App> = emptyList(),
    val frequencies: List<App> = emptyList(),
    val remindDaysBefore: List<App> = emptyList(),
    val showAppPicker: Boolean = false,
    val showCategoryPicker: Boolean = false,
    val showFrequencyPicker: Boolean = false,
    val showAmountPicker: Boolean = false,
    val showDatePicker: Boolean = false,
    val showReminderPicker: Boolean = false
) {
    val canSave: Boolean get() = subscription.amount > 0 && subscription.app.name.isNotBlank()
}

class EditSubscriptionViewModel(private val repository: SubscriptionRepository) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState = MutableStateFlow(EditSubscriptionUiState())
    val uiState: StateFlow<EditSubscriptionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apps = repository.getApps(),
                    categories = repository.getCategories(),
                    frequencies = repository.getFrequencies(),
                    remindDaysBefore = repository.remindDaysBefore()
                )
            }
        }
    }

    fun updateSubscription(subscription: Subscription) {
        _uiState.update { it.copy(subscription = subscription) }
    }

    fun saveSubscription() {
        viewModelScope.launch {
            repository.saveSubscription(uiState.value.subscription)
        }
    }

    fun deleteSubscription() {
        viewModelScope.launch {
            repository.deleteSubscription(uiState.value.subscription.id)
        }
    }

    fun setPickerVisibility(picker: Picker, visible: Boolean) {
        _uiState.update {
            when (picker) {
                Picker.APP -> it.copy(showAppPicker = visible)
                Picker.CATEGORY -> it.copy(showCategoryPicker = visible)
                Picker.FREQUENCY -> it.copy(showFrequencyPicker = visible)
                Picker.AMOUNT -> it.copy(showAmountPicker = visible)
                Picker.DATE -> it.copy(showDatePicker = visible)
                Picker.REMIND_ME -> it.copy(showReminderPicker = visible)
                else -> it
            }
        }
    }
}