package com.zacbrannelly.shoppingbuddy.ui.form

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zacbrannelly.shoppingbuddy.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class RecipeFormViewModel(application: Application): AndroidViewModel(application) {

    val recipe = MutableLiveData<FullRecipe>()

    private var imagePath: String? = null
    private val recipeRepository: RecipeRepository
    private val ingredientRepository: IngredientRepository
    private val stepRepository: StepRepository

    init {
        val db = AppDatabase.getInstance(application)
        recipeRepository = RecipeRepository(db.recipeDao())
        ingredientRepository = IngredientRepository(db.ingredientDao())
        stepRepository = StepRepository(db.stepDao())
    }

    fun loadRecipe(data: FullRecipe) {
        recipe.value = data
    }

    fun onBitmapLoaded(bitmap: Bitmap) = viewModelScope.launch(Dispatchers.IO) {
        val application = getApplication<Application>()
        imagePath = "${UUID.randomUUID()}.jpg"

        if (recipe.value != null) {
            val recipe = recipe.value!!
            if (!recipe.recipe.isImageAsset) {
                val existingImage = File(application.filesDir, recipe.recipe.image)
                if (existingImage.exists()) existingImage.delete()
            }
        }

        try {
            // Save bitmap to data folder.
            val stream = FileOutputStream(File(application.filesDir, imagePath))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save image to file.")
            imagePath = null
        }
    }

    fun onSave(name: String, type: String, ingredients: List<List<String>>, steps: List<List<String>>) {
        if (recipe.value == null) {
            onCreateNew(name, type, ingredients, steps)
        } else {
            onUpdateExisting(name, type, ingredients, steps)
        }
    }

    private fun onCreateNew(name: String, type: String, ingredients: List<List<String>>, steps: List<List<String>>) {
        // Write to the DB in the IO thread.
        viewModelScope.launch(Dispatchers.IO) {
            val icon = if (imagePath.isNullOrEmpty()) "placeholder.jpg" else imagePath!!
            val recipe = Recipe(UUID.randomUUID(), name, type, icon, imagePath.isNullOrEmpty())

            val recipeIngredients = ArrayList<Ingredient>()
            val joinRecords = ArrayList<RecipeIngredient>()

            // Map each field to an ingredient.
            mapFieldsToIngredients(recipe, recipeIngredients, joinRecords, ingredients)

            // Map step fields from view --> Step entities
            val recipeSteps = steps.mapIndexed { i, desc -> Step(recipe.id, i, desc[0]) }

            // Insert all the information required for the recipe into the DB.
            recipeRepository.insertRecipe(recipe)
            ingredientRepository.insertAll(recipeIngredients)
            ingredientRepository.insertAllJoins(joinRecords)
            stepRepository.insertAll(recipeSteps)
        }
    }

    private suspend fun mapFieldsToIngredients(
        recipe: Recipe,
        recipeIngredients: ArrayList<Ingredient>,
        joinRecords: ArrayList<RecipeIngredient>,
        fields: List<List<String>>) {

        for (ingredientFields in fields) {
            val name = ingredientFields[0]
            val qtyAndUnits = ingredientFields[1].split(" ")
            val units = qtyAndUnits[1]
            val qty = qtyAndUnits[0].toDouble()

            // Try to find the ingredient in the DB.
            var ingredient = ingredientRepository.findByNameAndUnits(name, units)

            if (ingredient == null) {
                ingredient = Ingredient(UUID.randomUUID(), name, units)
            }

            recipeIngredients.add(ingredient)

            // Join Recipe -> Ingredient with quantity via join table
            joinRecords.add(RecipeIngredient(recipe.id, ingredient.id, qty))
        }
    }

    private fun onUpdateExisting(name: String, type: String, ingredients: List<List<String>>, steps: List<List<String>>) {
        // Update existing items in the DB in IO thread.
        viewModelScope.launch(Dispatchers.IO) {
            val fullRecipe = recipe.value!!

            // Update the ame and type.
            fullRecipe.recipe.name = name
            fullRecipe.recipe.type = type

            // Update the image.
            if (!imagePath.isNullOrEmpty()) {
                fullRecipe.recipe.image = imagePath!!
                fullRecipe.recipe.isImageAsset = false
            }

            val newIngredients = ArrayList<Ingredient>()
            val newJoins = ArrayList<RecipeIngredient>()
            mapFieldsToIngredients(fullRecipe.recipe, newIngredients, newJoins, ingredients)

            // Map step fields from view --> Step entities
            val recipeSteps = steps.mapIndexed { i, desc -> Step(fullRecipe.recipe.id, i, desc[0]) }

            // Update recipe
            recipeRepository.updateRecipe(fullRecipe.recipe)

            // Insert all the new ingredients
            ingredientRepository.insertAll(newIngredients)

            // Clear joins and insert new updated joins
            ingredientRepository.clearAndInsertAllJoins(fullRecipe.recipe.id, newJoins)

            // Clear and insert steps
            stepRepository.clearAndInsertAll(fullRecipe.recipe.id, recipeSteps)
        }
    }

    companion object {
        private const val TAG = "RecipeFormViewModel"
    }
}