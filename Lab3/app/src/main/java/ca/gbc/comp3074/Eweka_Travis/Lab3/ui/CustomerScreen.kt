package ca.gbc.comp3074.Eweka_Travis.Lab3.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.gbc.comp3074.Eweka_Travis.Lab3.data.Customer

/**
 * Collects the Room Flows from the ViewModel and passes plain data down to the screen.
 */
@Composable
fun CustomerApp(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.searchResults
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val activeSearch by viewModel.searchText.collectAsStateWithLifecycle()

    CustomerScreen(
        customers = customers,
        activeSearch = activeSearch,
        onAddCustomer = viewModel::addCustomer,
        onDeleteCustomer = viewModel::deleteCustomer,
        onSearch = viewModel::search,
        onClearSearch = viewModel::clearSearch,
        modifier = modifier
    )
}

@Composable
fun CustomerScreen(
    customers: List<Customer>,
    activeSearch: String,
    onAddCustomer: (String, Int, Boolean) -> Unit,
    onDeleteCustomer: (Customer) -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Form state belongs to the UI; the saved list comes from Room
    var name by rememberSaveable { mutableStateOf("") }
    var ageText by rememberSaveable { mutableStateOf("") }
    var isActive by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var searchInput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Room Customer Demo",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Customer name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ageText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    ageText = newValue
                }
            },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Active customer")
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it }
            )
        }

        Button(
            onClick = {
                val age = ageText.toIntOrNull()
                if (name.isBlank() || age == null) {
                    message = "Enter a valid name and age."
                } else {
                    onAddCustomer(name.trim(), age, isActive)
                    name = ""
                    ageText = ""
                    isActive = false
                    message = "Customer submitted."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Customer")
        }

        message?.let {
            Spacer(Modifier.height(8.dp))
            Text(it)
        }

        Spacer(Modifier.height(12.dp))

        // Student challenge: search by name
        OutlinedTextField(
            value = searchInput,
            onValueChange = { searchInput = it },
            label = { Text("Search by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onSearch(searchInput) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Search")
            }
            OutlinedButton(
                onClick = {
                    searchInput = ""
                    onClearSearch()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }

        if (activeSearch.isNotBlank()) {
            Text(
                text = "Showing results for \"$activeSearch\"",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(12.dp))

        CustomerList(
            customers = customers,
            onDeleteCustomer = onDeleteCustomer,
            emptyText = if (activeSearch.isBlank()) "No customers in the database."
            else "No customers match your search.",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CustomerList(
    customers: List<Customer>,
    onDeleteCustomer: (Customer) -> Unit,
    modifier: Modifier = Modifier,
    emptyText: String = "No customers in the database."
) {
    if (customers.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(emptyText)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = customers,
                key = { customer -> customer.id }
            ) { customer ->
                CustomerRow(
                    customer = customer,
                    onDelete = { onDeleteCustomer(customer) }
                )
            }
        }
    }
}

@Composable
fun CustomerRow(
    customer: Customer,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("ID: ${customer.id}")
                Text("Age: ${customer.age}")
                Text(if (customer.isActive) "Active customer" else "Inactive customer")
            }
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}
