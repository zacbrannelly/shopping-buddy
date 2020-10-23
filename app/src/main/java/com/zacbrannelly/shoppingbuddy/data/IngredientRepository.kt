package com.zacbrannelly.shoppingbuddy.data

import java.util.*

class IngredientRepository(private val dao: IngredientDao) {
    suspend fun insertAllJoins(joins: List<RecipeIngredient>) = dao.insertAllJoins(joins)
    suspend fun clearAndInsertAllJoins(recipeId: UUID, joins: List<RecipeIngredient>) = dao.clearAndInsertAllJoins(recipeId, joins)

    suspend fun insertAll(ingredients: List<Ingredient>) = dao.insertAll(ingredients)
    suspend fun findByNameAndUnits(name: String, units: String) = dao.findByNameAndUnits(name, units)
}