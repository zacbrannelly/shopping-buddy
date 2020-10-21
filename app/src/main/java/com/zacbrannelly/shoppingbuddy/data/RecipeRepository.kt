package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import java.util.*

class RecipeRepository(private val dao: RecipeDao) {
    val recipesWithIngredients: LiveData<List<RecipeWithIngredients>> = dao.getAllRecipesWithIngredients()

    suspend fun insertRecipe(recipe: Recipe) = dao.insert(recipe)

    suspend fun findFullRecipe(id: UUID): FullRecipe? = dao.findFullRecipe(id)

    suspend fun deleteRecipe(recipe: Recipe) = dao.delete(recipe)
}