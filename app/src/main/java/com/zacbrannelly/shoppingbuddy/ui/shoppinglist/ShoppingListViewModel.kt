package com.zacbrannelly.shoppingbuddy.ui.shoppinglist

import android.app.Application
import androidx.lifecycle.*
import com.zacbrannelly.shoppingbuddy.data.ShoppingListRepository
import com.zacbrannelly.shoppingbuddy.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    val listItems = MutableLiveData<List<ShoppingListItem>>()

    private val shoppingListRepository: ShoppingListRepository

    init {
        val db = AppDatabase.getInstance(application)
        shoppingListRepository = ShoppingListRepository(db.shoppingListDao())

        // Generate shopping list from DB.
        viewModelScope.launch {
            val task = async(Dispatchers.IO) {
                shoppingListRepository.getShoppingList()
            }

            listItems.value = task.await()
        }
    }

    fun queryIngredients(query: String?) {
        // Either search or get the full list back depending on query's value.
        viewModelScope.launch {
            val task = async(Dispatchers.IO) {
                if (query.isNullOrBlank()) {
                    shoppingListRepository.getShoppingList()
                } else {
                    shoppingListRepository.searchShoppingList(query)
                }
            }

            listItems.value = task.await()
        }
    }

    fun updateItem(ingredient: Ingredient, checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingListRepository.updateStatus(ShoppingList(ingredient.id, checked))
        }
    }
}