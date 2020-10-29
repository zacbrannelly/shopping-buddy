package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingList>)

    @Query("DELETE FROM shopping_list")
    suspend fun deleteAll()

    @Query("SELECT i.*, s.is_checked as isChecked, SUM(ri.qty) as qty FROM planners AS p " +
            "INNER JOIN recipes AS r ON r.id = p.recipe_id " +
            "INNER JOIN recipe_ingredients AS ri ON r.id = ri.recipe_id " +
            "INNER JOIN ingredients AS i ON ri.ingredient_id = i.id " +
            "LEFT JOIN shopping_list as s ON s.ingredientId = i.id " +
            "GROUP BY i.id, ri.qty"
    )
    suspend fun getShoppingList(): List<ShoppingListItem>

    @Query("SELECT i.*, s.is_checked as isChecked, SUM(ri.qty) as qty FROM planners AS p " +
            "INNER JOIN recipes AS r ON r.id = p.recipe_id " +
            "INNER JOIN recipe_ingredients AS ri ON r.id = ri.recipe_id " +
            "INNER JOIN ingredients AS i ON ri.ingredient_id = i.id " +
            "LEFT JOIN shopping_list as s ON s.ingredientId = i.id " +
            "WHERE i.name LIKE '%' || :query || '%'" +
            "GROUP BY i.id, ri.qty"
    )
    suspend fun searchShoppingList(query: String): List<ShoppingListItem>

    @Transaction
    suspend fun clearAndInsertAll(items: List<ShoppingList>) {
        deleteAll()
        insertAll(items)
    }
}