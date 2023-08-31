package com.pavel.scopictask.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavel.scopictask.data.network.model.Response
import com.pavel.scopictask.data.network.model.ListItem
import com.pavel.scopictask.data.repositories.ListItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListItemViewModel @Inject constructor(
    private val listItemRepository: ListItemRepository
) : ViewModel() {

    private val _items = MutableStateFlow<Response<List<ListItem>>>(Response.Loading)
    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            listItemRepository.getItems().collect{ items ->
                _items.value = items
            }
        }
    }

    fun insertItem(itemName: String) = viewModelScope.launch {
        listItemRepository.insertItem(itemName)
    }

    fun updateItem(updateItem: ListItem) = viewModelScope.launch {
        listItemRepository.updateListItem(updateItem)
    }

    fun deleteItem(itemId: String) = viewModelScope.launch {
        listItemRepository.deleteItem(itemId)
    }

}