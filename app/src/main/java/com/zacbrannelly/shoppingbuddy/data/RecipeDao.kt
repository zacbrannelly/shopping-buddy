package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getRecipes(): LiveData<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    @Query("DELETE FROM recipes")
    suspend fun deleteAll()
}