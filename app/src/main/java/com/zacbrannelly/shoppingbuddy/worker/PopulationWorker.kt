package com.zacbrannelly.shoppingbuddy.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.zacbrannelly.shoppingbuddy.data.*
import kotlinx.coroutines.coroutineScope
import java.lang.Exception

const val INITIAL_DATA_FILE = "data.json"

class PopulationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(INITIAL_DATA_FILE).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val type = object : TypeToken<List<FullRecipe>>() {}.type
                    val recipes = Gson().fromJson<List<FullRecipe>>(jsonReader, type)

                    val database = AppDatabase.getInstance(applicationContext)

                    // Insert the recipes into the DB.
                    database.recipeDao().insertAll(recipes.map { it.recipe })

                    // Insert the recipes steps into the DB.
                    database.stepDao().insertAll(recipes.flatMap { recipe ->
                        recipe.steps.map {
                            // Map step to recipe ID.
                            return@map Step(
                                recipe.recipe.id,
                                it.step,
                                it.description
                            )
                        }
                    })

                    // Insert the recipe ingredients into the DB.
                    database.ingredientDao().insertAll(recipes.flatMap { recipe ->
                        recipe.ingredients.map { it.ingredient }
                    })

                    // Insert the links b/w recipe and ingredients into the DB.
                    database.recipeIngredientDao().insertAll(recipes.flatMap { recipe ->
                        recipe.ingredients.map {
                            // Map recipe to ingredient and quantity.
                            return@map RecipeIngredient(
                                recipe.recipe.id,
                                it.ingredient.id,
                                it.metadata.qty
                            )
                        }
                    })

                    Result.success()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error populating DB: ", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "LoaderWorker"
    }
}
