package com.zacbrannelly.shoppingbuddy.data

import androidx.room.*
import java.util.*

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(steps: List<Step>)

    @Query("DELETE FROM steps WHERE recipe_id=:recipeId")
    suspend fun deleteAll(recipeId: UUID)

    @Transaction
    suspend fun clearAndInsertAll(recipeId: UUID, steps: List<Step>) {
        deleteAll(recipeId)
        insertAll(steps)
    }
}