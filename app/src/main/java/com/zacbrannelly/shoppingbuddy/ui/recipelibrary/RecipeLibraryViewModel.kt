package com.zacbrannelly.shoppingbuddy.ui.recipelibrary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.zacbrannelly.shoppingbuddy.data.AppDatabase
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.data.RecipeRepository
import com.zacbrannelly.shoppingbuddy.data.RecipeWithIngredients
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RecipeLibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository: RecipeRepository
    private var recipeCache: List<RecipeWithIngredients>? = null

    val recipes = MediatorLiveData<List<RecipeWithIngredients>>()

    init {
        // Create the recipe repository to start accessing recipes.
        val recipeDao = AppDatabase.getInstance(application).recipeDao()
        recipeRepository = RecipeRepository(recipeDao)

        recipes.addSource(recipeRepository.recipesWithIngredients) { values ->
            recipes.value = values
            recipeCache = values
        }
    }

    fun queryRecipes(query: String?) {
        if (query.isNullOrBlank()) {
            recipes.value = recipeCache
        } else {
            viewModelScope.launch(Dispatchers.Main) {
                val task = async(Dispatchers.IO) {
                    recipeRepository.searchRecipeWithIngredients(query)
                }

                recipes.value = task.await()
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.deleteRecipe(recipe)
        }
    }
}