package com.zacbrannelly.shoppingbuddy.ui.recipelibrary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.zacbrannelly.shoppingbuddy.data.AppDatabase
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.data.RecipeRepository

class RecipeLibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository: RecipeRepository

    val recipes: LiveData<List<Recipe>>

    init {
        // Create the recipe repository to start accessing recipes.
        val recipeDao = AppDatabase.getInstance(application).recipeDao()
        recipeRepository = RecipeRepository(recipeDao)
        recipes = recipeRepository.recipes
    }
}