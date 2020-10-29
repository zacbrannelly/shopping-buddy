package com.zacbrannelly.shoppingbuddy.data

class ShoppingListRepository(private val dao: ShoppingListDao) {
    suspend fun getShoppingList() = dao.getShoppingList()
    suspend fun searchShoppingList(query: String) = dao.searchShoppingList(query)

    suspend fun updateStatus(item: ShoppingList) = dao.insert(item)
    suspend fun clearAndInsertAll(items: List<ShoppingList>) = dao.clearAndInsertAll(items)
}