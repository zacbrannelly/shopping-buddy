package com.zacbrannelly.shoppingbuddy.data

import java.util.*

class StepRepository(private val dao: StepDao) {
    suspend fun insertAll(steps: List<Step>) = dao.insertAll(steps)
    suspend fun clearAndInsertAll(recipeId: UUID, steps: List<Step>) = dao.clearAndInsertAll(recipeId, steps)
}