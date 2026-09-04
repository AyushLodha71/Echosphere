package com.example.echosphere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echosphere.data.remote.RetrofitClient
import com.example.echosphere.data.remote.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results: StateFlow<List<SearchResult>> = _results.asStateFlow()

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) return

        _status.value = "Searching..."

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.search(query)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _results.value = body
                        _status.value = if (body.isEmpty()) "No results" else ""
                    } else {
                        _status.value = "Empty response"
                    }
                } else {
                    _status.value = "Search failed: ${response.code()}"
                }
            } catch (e: Exception) {
                android.util.Log.e("SearchViewModel", "Search error", e)
                _status.value = "Network error: ${e.message}"
            }
        }
    }
}