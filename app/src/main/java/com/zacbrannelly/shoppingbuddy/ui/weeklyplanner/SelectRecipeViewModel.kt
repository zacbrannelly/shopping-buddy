package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.app.Application
import androidx.lifecycle.*
import com.zacbrannelly.shoppingbuddy.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectRecipeViewModel(application: Application): AndroidViewModel(application) {

    var recipes: MediatorLiveData<MutableList<RecipeWithIngredients>>
    var selectedRecipes: MutableLiveData<ArrayList<RecipeWithIngredients>>

    private val recipeRepository: RecipeRepository
    private val plannerRepository: PlannerRepository
    private val recipeCache: ArrayList<RecipeWithIngredients>

    init {
        val db = AppDatabase.getInstance(application)
        recipeRepository = RecipeRepository(db.recipeDao())
        plannerRepository = PlannerRepository(db.plannerDao())
        recipeCache = ArrayList()

        recipes = MediatorLiveData()
        selectedRecipes = MutableLiveData(ArrayList())

        // Use MediatorLiveData so we can create a mutable list of the DB version.
        recipes.addSource(recipeRepository.recipesWithIngredients) {
            recipes.value = it.toMutableList()
        }
    }

    fun onRecipeSearch(query: String?) {
        // Reset when the query string is empty.
        if (query.isNullOrEmpty() && recipeCache.isNotEmpty()) {
            // Reset the recipe list with the cache.
            recipes.value?.clear()
            recipes.value?.addAll(recipeCache)

            // Clear the cache.
            recipeCache.clear()

            // Notify the observers of the change.
            recipes.value = recipes.value
            selectedRecipes.value = selectedRecipes.value
            return
        }

        // Cache the existing recipes.
        if (recipeCache.isEmpty()) {
            if (!recipes.value.isNullOrEmpty()) {
                recipeCache.addAll(recipes.value!!)
            }
        }

        // Filter the cache by the search phrase and notify observers.
        recipes.value = recipeCache.filter { it.recipe.name.toLowerCase().contains(query!!.toLowerCase()) }.toMutableList()
        selectedRecipes.value = selectedRecipes.value
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