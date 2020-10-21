package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients")
    fun getIngredients(): LiveData<List<Ingredient>>

    @Query("SELECT * FROM ingredients WHERE name=:name AND units=:units")
    suspend fun findByNameAndUnits(name: String, units: String): Ingredient?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<Ingredient>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllJoins(recipeIngredient: List<RecipeIngredient>)
}