package com.zacbrannelly.shoppingbuddy.ui.detail

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
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
    var expandableListItems: MediatorLiveData<List<ExpandableListItem>> = MediatorLiveData()

    fun loadRecipe(data: Recipe) {
        // Retrieve the full recipe as live data, so updates are shown
        val liveData = recipeRepository.findFullRecipe(data.id)

        val transformation = Transformations.switchMap(liveData) { fullRecipe ->
            viewModelScope.launch(Dispatchers.IO) {
                // Load the image attached to the recipe and show on screen.
                val bitmap = fullRecipe.recipe.loadBitmap(getApplication())
                recipeImage.postValue(bitmap)
            }

            // Update the recipe shown on the screen.
            recipe.value = fullRecipe

            val items = mutableListOf<ExpandableListItem>()

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

            MutableLiveData<List<ExpandableListItem>>(items)
        }

        // Update the expandable list when updates occur
        expandableListItems.addSource(transformation) {
            expandableListItems.value = it
        }
    }
}