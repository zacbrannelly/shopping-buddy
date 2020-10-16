package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlannerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(planners: List<Planner>)

    @Transaction
    @Query("SELECT * FROM planners")
    fun getAllPlannersWithRecipes(): LiveData<List<PlannerWithRecipe>>

    @Transaction
    @Query("DELETE FROM planners")
    suspend fun deleteAll()
}