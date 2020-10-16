package com.zacbrannelly.shoppingbuddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.zacbrannelly.shoppingbuddy.data.population.PopulationWorker

const val DATABASE_NAME = "shopping_buddy"

@Database(
    entities = [
        Recipe::class,
        RecipeIngredient::class,
        Ingredient::class,
        Step::class,
        Planner::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    abstract fun ingredientDao(): IngredientDao

    abstract fun recipeIngredientDao(): RecipeIngredientDao

    abstract fun stepDao(): StepDao

    abstract fun plannerDao(): PlannerDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // Trigger population of the database
                        val request = OneTimeWorkRequestBuilder<PopulationWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .build()
        }
    }
}