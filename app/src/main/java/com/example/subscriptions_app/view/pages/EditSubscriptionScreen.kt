package com.example.subscriptions_app.view.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subscriptions_app.R
import com.example.subscriptions_app.model.App
import com.example.subscriptions_app.model.Subscription
import com.example.subscriptions_app.core.theme.GraphikTrial
import com.example.subscriptions_app.core.theme.Subscriptions_appTheme
import com.example.subscriptions_app.view.component.EditViewModelFactoryProvider
import com.example.subscriptions_app.viewmodel.EditSubscriptionViewModel
import com.example.subscriptions_app.viewmodel.Picker
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(
    vm: EditSubscriptionViewModel = viewModel(factory = EditViewModelFactoryProvider.provide()),
    onClose: () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val sub = uiState.subscription
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Subscriptions_appTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Edit Subscription", fontSize = 18.sp,
                        color = Color(0xFF1C1E22),
                        fontWeight = FontWeight.Medium, fontFamily = GraphikTrial) },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                tint = Color.Unspecified,
                                modifier = Modifier.size(44.dp),
                                contentDescription = "Back")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            vm.saveSubscription()
                            onClose()
                        }) {
                            Text("Save", fontWeight = FontWeight.Medium, color = Color(0xFF002FFF),
                                fontFamily = GraphikTrial
                                )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                SubscriptionSummaryCard(subscription = sub)

                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ){
                    InputSection("App", sub.app.name,true,-1) {
                        vm.setPickerVisibility(Picker.APP, true)
                    }
                    InputSection("Amount", "$${sub.amount}",false,-1) { vm.setPickerVisibility(Picker.AMOUNT, true) }
                    InputSection("Category", sub.category.name,true,sub.category.icon) { vm.setPickerVisibility(Picker.CATEGORY, true) }

                }
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ){
                    InputSection("Start Date", formatDate(sub.startDate),false,-1) { vm.setPickerVisibility(Picker.DATE, true) }
                    InputSection("Frequency", sub.frequency.name,true,-1) { vm.setPickerVisibility(Picker.FREQUENCY, true) }
                    InputSection("Remind me", sub.remindDaysBefore.name,true,-1) { vm.setPickerVisibility(Picker.REMIND_ME, true) }
                    SwitchRow(
                        title = "Active",
                        checked = sub.active,
                        onCheckedChange = { vm.updateSubscription(sub.copy(active = it)) }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        vm.deleteSubscription()
                        onClose()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp), // corner radius
                    border = BorderStroke(1.dp, Color(0XFFE6E8EB)) //
                ) {
                    Text("Delete", color = Color(0XFFFF3F3F),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // ==================== App Pickers ====================
        if (uiState.showAppPicker) {
            ModalBottomSheet(
                onDismissRequest = { vm.setPickerVisibility(Picker.APP, false) },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                WheelPickerSheet(
                    title = "App",
                    options = uiState.apps,
                    selected = sub.app.name,
                    onSelect = { index -> vm.updateSubscription(sub.copy(app = uiState.apps[index])) },
                    onDone = {
                        coroutineScope.launch { sheetState.hide() }
                            .invokeOnCompletion { vm.setPickerVisibility(Picker.APP, false) }
                    },
                    true
                )
            }
        }

        if (uiState.showCategoryPicker) {
            ModalBottomSheet(
                onDismissRequest = { vm.setPickerVisibility(Picker.CATEGORY, false) },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                WheelPickerSheet(
                    title = "Category",
                    options = uiState.categories,
                    selected = sub.category.name,
                    onSelect = { index -> vm.updateSubscription(sub.copy(category = uiState.categories[index])) },
                    onDone = {
                        coroutineScope.launch { sheetState.hide() }
                            .invokeOnCompletion { vm.setPickerVisibility(Picker.CATEGORY, false) }
                    }
                    ,false
                )
            }
        }

        if (uiState.showFrequencyPicker) {
            ModalBottomSheet(
                onDismissRequest = { vm.setPickerVisibility(Picker.FREQUENCY, false) },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                WheelPickerSheet(
                    title = "Frequency",
                    options = uiState.frequencies,
                    selected = sub.frequency.name,
                    onSelect = { index -> vm.updateSubscription(sub.copy(frequency = uiState.frequencies[index])) },
                    onDone = {
                        coroutineScope.launch { sheetState.hide() }
                            .invokeOnCompletion { vm.setPickerVisibility(Picker.FREQUENCY, false) }
                    },
                    false
                )
            }
        }

        if (uiState.showReminderPicker) {
            ModalBottomSheet(
                onDismissRequest = { vm.setPickerVisibility(Picker.FREQUENCY, false) },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                WheelPickerSheet(
                    title = "Remind me",
                    options = uiState.remindDaysBefore,
                    selected = sub.remindDaysBefore.name,
                    onSelect = { index -> vm.updateSubscription(sub.copy(remindDaysBefore = uiState.remindDaysBefore[index])) },
                    onDone = {
                        coroutineScope.launch { sheetState.hide() }
                            .invokeOnCompletion { vm.setPickerVisibility(Picker.REMIND_ME, false) }
                    },
                    false
                )
            }
        }

        if (uiState.showDatePicker) {
            ModalBottomSheet(
                onDismissRequest = { vm.setPickerVisibility(Picker.DATE, false) },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                DateWheelPickerSheet(
                    selectedDate = sub.startDate,
                    onDateSelected = { newDate -> vm.updateSubscription(sub.copy(startDate = newDate)) },
                    onDone = {
                        coroutineScope.launch { sheetState.hide() }
                            .invokeOnCompletion { vm.setPickerVisibility(Picker.DATE, false) }
                    }
                )
            }
        }
    }
}

@Composable
fun WheelPickerSheet(
    title: String,
    options: List<App>,
    selected: String,
    onSelect: (Int) -> Unit,
    onDone: () -> Unit,
    isSearch: Boolean,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredOptions = options.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Drag handle
        /*Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(50))
                .align(Alignment.CenterHorizontally)
        )*/

        //Spacer(modifier = Modifier.height(8.dp))

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = GraphikTrial)
            TextButton(onClick = onDone) { Text("Done", fontWeight = FontWeight.SemiBold) }
        }

        //Spacer(modifier = Modifier.height(8.dp))

        // Search bar
        if(isSearch) OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF4A90E2),
                focusedIndicatorColor = Color(0xFFD0D5DD),   // ✅ Focused border
                unfocusedIndicatorColor = Color(0xFFD0D5DD), // ✅ Unfocused border
                focusedLabelColor = Color(0xFF4A90E2)
            )
        )


        if(isSearch) Spacer(modifier = Modifier.height(8.dp))

        // Options list with icons
        LazyColumn {
            items(filteredOptions.size) { index ->
                val option = filteredOptions[index]
                val isSelected = option.name == selected

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val originalIndex = options.indexOf(option)
                            if (originalIndex != -1) onSelect(originalIndex)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Icon
                    if (option.icon != -1)Icon(
                        painter = painterResource(id = option.icon),
                        contentDescription = option.name,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )

                    if (option.icon != -1) Spacer(modifier = Modifier.width(12.dp))

                    // App Name
                    Text(
                        text = option.name,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1C1E22),
                        fontFamily = GraphikTrial
                    )

                    // Checkmark if selected
                    if (isSelected) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_checked),
                            contentDescription = null,
                            tint = Color.Blue
                        )
                    }

                }
                if (index < filteredOptions.lastIndex)
                HorizontalDivider(
                    thickness = 1.dp,        // you can adjust thickness
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFE6E8EB))
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateWheelPickerSheet(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDone: () -> Unit
) {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val years = (selectedDate.year - 10..selectedDate.year + 10).toList()

    var selectedMonth by remember { mutableStateOf(selectedDate.monthValue - 1) }
    var selectedDay by remember { mutableStateOf(selectedDate.dayOfMonth) }
    var selectedYear by remember { mutableStateOf(selectedDate.year) }

    val maxDays = YearMonth.of(selectedYear, selectedMonth + 1).lengthOfMonth()
    if (selectedDay > maxDays) selectedDay = maxDays

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Handle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Text("Start Date", fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = GraphikTrial)
            TextButton(onClick = onDone) { Text("Done", fontWeight = FontWeight.SemiBold) }
        }
        // Header
        Spacer(modifier = Modifier.height(8.dp))

        // Picker row with highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WheelColumn(
                    items = months,
                    selectedIndex = selectedMonth,
                    onSelect = { idx -> selectedMonth = idx }
                )
                WheelColumn(
                    items = (1..maxDays).map { it.toString() },
                    selectedIndex = selectedDay - 1,
                    onSelect = { idx -> selectedDay = idx + 1 }
                )
                WheelColumn(
                    items = years.map { it.toString() },
                    selectedIndex = years.indexOf(selectedYear),
                    onSelect = { idx -> selectedYear = years[idx] }
                )
            }
        }
    }
}

@Composable
fun WheelColumn(
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)

    Box(
        modifier = Modifier
            .width(100.dp)
            .fillMaxHeight()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items.size) { index ->
                val isSelected = listState.firstVisibleItemIndex == index
                Text(
                    text = items[index],
                    fontSize = if (isSelected) 20.sp else 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .clickable { onSelect(index) }
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(36.dp)
                .background(Color.LightGray.copy(alpha = 0.2f))
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return date.format(formatter)
}

@Composable
fun InputSection(title: String, value: String,isShowIcons: Boolean,icon:Int, onClick: () -> Unit ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF636A79),
            fontFamily = GraphikTrial
        )
        Row(verticalAlignment = Alignment.CenterVertically)
        {

            if (icon!=-1) {
                Icon(
                    painter = painterResource(id = icon),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(18.dp),
                    contentDescription = "More")
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(value, fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1C1E22),
                fontFamily = GraphikTrial
            )

           if (isShowIcons) {
               Spacer(modifier = Modifier.width(4.dp))
               Icon(
                painter = painterResource(id = R.drawable.ic_more),
                tint = Color.Unspecified,
                   modifier = Modifier.size(18.dp),
                contentDescription = "More")
           }
        }

    }
    HorizontalDivider(
        thickness = 1.dp,        // you can adjust thickness
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE6E8EB))
}

@Composable
fun SwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF636A79),
            fontFamily = GraphikTrial
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,       // Thumb color when checked,   // Thumb color when checked
                uncheckedThumbColor = Color.White,       // Thumb color when unchecked
                checkedTrackColor = Color(0xFF65C466),  // Track color when checked
                uncheckedTrackColor = Color.Gray   // Track color when unchecked
            )
            )
    }
}

@Composable
fun SubscriptionSummaryCard(subscription: Subscription) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = subscription.app.icon),
                    contentDescription = "App Icon",
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(subscription.app.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    fontFamily = GraphikTrial,
                    color = Color(0xFF1C1E22)

                    )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$${subscription.amount} / ${subscription.frequency.name.lowercase()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF636A79),
                    fontFamily = GraphikTrial

                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewEditSubscriptionScreen() {
    EditSubscriptionScreen(onClose = {})
}
