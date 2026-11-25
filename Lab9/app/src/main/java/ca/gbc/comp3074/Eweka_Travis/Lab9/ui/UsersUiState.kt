package ca.gbc.comp3074.Eweka_Travis.Lab9.ui

import ca.gbc.comp3074.Eweka_Travis.Lab9.model.User

sealed interface UsersUiState {
    data object Loading : UsersUiState
    data class Success(val users: List<User>) : UsersUiState
    data class Error(val message: String) : UsersUiState
}
