package ca.gbc.comp3074.Eweka_Travis.Lab3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.Eweka_Travis.Lab3.data.Customer
import ca.gbc.comp3074.Eweka_Travis.Lab3.data.CustomerDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * Keeps database work out of the composables.
 * Reads come from Room as Flow; writes are launched in viewModelScope.
 */
class CustomerViewModel(
    private val customerDao: CustomerDao
) : ViewModel() {

    // Every customer, kept up to date by Room
    val customers: Flow<List<Customer>> = customerDao.getAllCustomers()

    // Student challenge: search. Blank search text = show everyone.
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<List<Customer>> = _searchText.flatMapLatest { text ->
        if (text.isBlank()) customerDao.getAllCustomers()
        else customerDao.searchCustomers(text.trim())
    }

    fun search(text: String) {
        _searchText.value = text
    }

    fun clearSearch() {
        _searchText.value = ""
    }

    fun addCustomer(name: String, age: Int, isActive: Boolean) {
        viewModelScope.launch {
            customerDao.insert(
                Customer(
                    name = name,
                    age = age,
                    isActive = isActive
                )
            )
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            customerDao.delete(customer)
        }
    }
}

/**
 * CustomerViewModel needs a CustomerDao, so this factory tells Android how to build it.
 */
class CustomerViewModelFactory(
    private val customerDao: CustomerDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            return CustomerViewModel(customerDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
