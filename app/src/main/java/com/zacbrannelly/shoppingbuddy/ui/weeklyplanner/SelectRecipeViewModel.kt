package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zacbrannelly.shoppingbuddy.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectRecipeViewModel(application: Application): AndroidViewModel(application) {

    var recipes: LiveData<List<RecipeWithIngredients>>
    var selectedRecipes: MutableLiveData<ArrayList<RecipeWithIngredients>>

    private val recipeRepository: RecipeRepository
    private val plannerRepository: PlannerRepository

    init {
        val db = AppDatabase.getInstance(application)
        recipeRepository = RecipeRepository(db.recipeDao())
        plannerRepository = PlannerRepository(db.plannerDao())
        recipes = recipeRepository.recipesWithIngredients
        selectedRecipes = MutableLiveData(ArrayList())
    }

    fun onRecipeSelected(recipe: RecipeWithIngredients) {
        val newList = selectedRecipes.value!!

        // If recipe is already selected, deselect, otherwise select.
        if (newList.contains(recipe)) {
            newList.remove(recipe)
        } else {
            newList.add(recipe)
        }

        selectedRecipes.value = newList
    }

    fun onConfirm() {
        // Insert each recipe into the unplanned day.
        viewModelScope.launch(Dispatchers.IO) {
            for (recipe in selectedRecipes.value!!) {
                plannerRepository.insertRecipePlan(Day.UNPLANNED, recipe.recipe)
            }
        }
    }
}