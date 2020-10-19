package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlannerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(planner: Planner)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(planners: List<Planner>)

    @Transaction
    suspend fun clearAndInsertAll(planners: List<Planner>) {
        deleteAll()
        insertAll(planners)
    }

    @Transaction
    @Query("SELECT * FROM planners")
    fun getAllPlannersWithRecipes(): LiveData<List<PlannerWithRecipe>>

    @Query("SELECT COUNT(*) FROM planners WHERE day = :day")
    fun getPlannerRecipeCount(day: Day): Int?

    @Transaction
    @Query("DELETE FROM planners")
    suspend fun deleteAll()
}