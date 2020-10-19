package com.zacbrannelly.shoppingbuddy.data

import androidx.lifecycle.*

class PlannerRepository(private val plannerDao: PlannerDao) {

    // Transforms LiveData<List<Planner>> --> LiveData<List<PlannerDay>>
    fun getPlannerDays(): LiveData<List<PlannerDay>> {
        return Transformations.switchMap(plannerDao.getAllPlannersWithRecipes()) { plannersWithRecipes ->
            // Sort the planner by priority values, to preserve order of the list.
            val sortedPlanner = plannersWithRecipes.sortedBy { it.planner.priority }

            val data = mutableMapOf<Day, ArrayList<RecipeWithIngredients>>()

            for (plannerWithRecipe in sortedPlanner) {
                val planner = plannerWithRecipe.planner

                if (!data.containsKey(planner.day)) {
                    data[planner.day] = ArrayList()
                }
                data[planner.day]?.add(plannerWithRecipe.recipe)
            }

            // Map the dictionary to a list of PlannerDay instances.
            MutableLiveData<List<PlannerDay>>(data.map { PlannerDay(it.key, it.value) })
        }
    }

    suspend fun insertRecipePlan(day: Day, recipe: Recipe) {
        val count = plannerDao.getPlannerRecipeCount(day)
        if (count != null) {
            val priority = count + 1
            plannerDao.insert(Planner(day, recipe.id, priority))
        }
    }

    suspend fun savePlannerDays(plannerDays: List<PlannerDay>) {
        val planners = plannerDays.flatMap { plannerDay ->
            var index = 1
            plannerDay.recipes.map { Planner(plannerDay.day, it.recipe.id, index++) }
        }

        plannerDao.clearAndInsertAll(planners)
    }

}