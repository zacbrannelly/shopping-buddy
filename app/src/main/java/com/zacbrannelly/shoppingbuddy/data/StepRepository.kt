package com.zacbrannelly.shoppingbuddy.data

class StepRepository(private val dao: StepDao) {

    suspend fun insertAll(steps: List<Step>) = dao.insertAll(steps)

}