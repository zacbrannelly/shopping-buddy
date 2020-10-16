package com.zacbrannelly.shoppingbuddy.ui.recipelibrary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.zacbrannelly.shoppingbuddy.data.AppDatabase
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.data.RecipeRepository
import com.zacbrannelly.shoppingbuddy.data.RecipeWithIngredients
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeLibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository: RecipeRepository

    val recipes: LiveData<List<RecipeWithIngredients>>

    init {
        // Create the recipe repository to start accessing recipes.
        val recipeDao = AppDatabase.getInstance(application).recipeDao()
        recipeRepository = RecipeRepository(recipeDao)
        recipes = recipeRepository.recipesWithIngredients
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.deleteRecipe(recipe)
        }
    }
}