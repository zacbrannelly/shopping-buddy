package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface RecipeDao {
    @Transaction
    @Query("SELECT * FROM recipes")
    fun getAllRecipesWithIngredients(): LiveData<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes")
    fun getAllFullRecipes(): LiveData<List<FullRecipe>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    fun findFullRecipe(id: UUID): FullRecipe?

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    fun findRecipeWithIngredients(id: UUID): RecipeWithIngredients?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("DELETE FROM recipes")
    suspend fun deleteAll()
}