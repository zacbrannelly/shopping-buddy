package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData

class RecipeRepository (private val dao: RecipeDao) {
    val recipes: LiveData<List<Recipe>> = dao.getRecipes()
}