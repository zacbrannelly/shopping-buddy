package com.zacbrannelly.shoppingbuddy.data

class IngredientRepository(private val dao: IngredientDao) {
    suspend fun insertAllJoins(joins: List<RecipeIngredient>) = dao.insertAllJoins(joins)
    suspend fun insertAll(ingredients: List<Ingredient>) = dao.insertAll(ingredients)
    suspend fun findByNameAndUnits(name: String, units: String) = dao.findByNameAndUnits(name, units)
}