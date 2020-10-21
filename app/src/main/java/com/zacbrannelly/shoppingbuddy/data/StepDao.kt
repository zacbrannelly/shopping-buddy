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
    suspend fun clearAndInsertAll(steps: List<Step>) {
        deleteAll(steps[0].recipeId)
        insertAll(steps)
    }
}