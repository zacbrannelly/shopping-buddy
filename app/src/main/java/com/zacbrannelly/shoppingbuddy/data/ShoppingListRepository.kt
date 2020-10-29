package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import com.zacbrannelly.shoppingbuddy.data.ShoppingList
import com.zacbrannelly.shoppingbuddy.data.ShoppingListDao

class ShoppingListRepository(private val dao: ShoppingListDao) {
    fun getShoppingList(): LiveData<List<ShoppingList>> = dao.getShoppingList()
    suspend fun clearAndInsertAll(items: List<ShoppingList>) = dao.clearAndInsertAll(items)
}