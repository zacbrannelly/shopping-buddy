package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import java.util.*

class RecipeRepository (private val dao: RecipeDao) {
    val recipesWithIngredients: LiveData<List<RecipeWithIngredients>> = dao.getAllRecipesWithIngredients()

    suspend fun insert(recipe: FullRecipe) {
        // TODO: Insert full recipe into the DB.
    }

    suspend fun findFullRecipe(id: UUID): FullRecipe? = dao.findFullRecipe(id)

    suspend fun deleteRecipe(recipe: Recipe) = dao.delete(recipe)
}