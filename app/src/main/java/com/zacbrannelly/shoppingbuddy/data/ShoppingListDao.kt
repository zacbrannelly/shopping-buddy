package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_list")
    fun getShoppingList(): LiveData<List<ShoppingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingList>)

    @Query("DELETE FROM shopping_list")
    suspend fun deleteAll()

    @Transaction
    suspend fun clearAndInsertAll(items: List<ShoppingList>) {
        deleteAll()
        insertAll(items)
    }

}