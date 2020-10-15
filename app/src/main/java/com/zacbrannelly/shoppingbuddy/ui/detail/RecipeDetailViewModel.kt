package com.zacbrannelly.shoppingbuddy.ui.detail

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.AppDatabase
import com.zacbrannelly.shoppingbuddy.data.FullRecipe
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.data.RecipeRepository
import com.zacbrannelly.shoppingbuddy.ui.ExpandableListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.round

class RecipeDetailViewModel(application: Application): AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val recipeRepository = RecipeRepository(database.recipeDao())

    var recipeImage: MutableLiveData<Bitmap> = MutableLiveData()
    var recipe: MutableLiveData<FullRecipe> = MutableLiveData()
    var expandableListItems: MutableLiveData<List<ExpandableListItem>> = MutableLiveData()

    fun loadRecipe(data: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch the full recipe (including ingredients and steps).
            val fullRecipe = recipeRepository.findFullRecipe(data.id) ?: return@launch

            // Load the image attached to the recipe.
            val bitmap = fullRecipe.recipe.loadBitmap(getApplication())

            val items = ArrayList<ExpandableListItem>()

            // Create expandable list of ingredients.
            val ingredients = fullRecipe.ingredients.map {
                val qty = ceil(it.metadata.qty).toInt()
                Pair(it.ingredient.name, "$qty ${it.ingredient.units}")
            }
            items.add(ExpandableListItem(R.drawable.ic_shopping_icon, "Ingredients", ingredients, true))

            // If no steps provided, skip making an expandable list.
            if (fullRecipe.steps.isNotEmpty()) {
                // Create expandable list of steps.
                val steps = fullRecipe.steps.map { Pair("Step ${it.step + 1}", it.description) }
                items.add(ExpandableListItem(R.drawable.ic_outdoor_grill, "Steps", steps, true))
            }

            // Safely set the results on a main thread.
            recipeImage.postValue(bitmap)
            recipe.postValue(fullRecipe)
            expandableListItems.postValue(items)
        }
    }
}