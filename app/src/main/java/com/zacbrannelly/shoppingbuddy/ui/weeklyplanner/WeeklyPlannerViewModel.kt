package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.zacbrannelly.shoppingbuddy.data.*
import com.zacbrannelly.shoppingbuddy.ui.RecipeListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "WeeklyPlannerViewModel"

class WeeklyPlannerViewModel(application: Application) : AndroidViewModel(application) {
    var recipes: LiveData<List<RecipeListItem>>

    private var planner = TreeMap<Day, List<RecipeWithIngredients>>(mapOf(
        Day.UNPLANNED to emptyList(),
        Day.MONDAY    to emptyList(),
        Day.TUESDAY   to emptyList(),
        Day.WEDNESDAY to emptyList(),
        Day.THURSDAY  to emptyList(),
        Day.FRIDAY    to emptyList(),
        Day.SATURDAY  to emptyList(),
        Day.SUNDAY    to emptyList()
    ))

    private val plannerRepository: PlannerRepository
    private val plannerDays: LiveData<List<PlannerDay>>

    init {
        // Fetch the DB instance and create planner repository.
        val db = AppDatabase.getInstance(application)
        plannerRepository = PlannerRepository(db.plannerDao())
        plannerDays = plannerRepository.getPlannerDays()

        // Transform from List<PlannerDay> -> List<RecipeListItem>
        recipes = Transformations.switchMap(plannerDays) { days ->
            val items = mutableListOf<RecipeListItem>()

            // If no planner present, clear out internal representation.
            if (days.isEmpty()) planner.keys.forEach { planner[it] = emptyList() }

            // Update internal representation of the planner with DB.
            days.forEach { day -> planner[day.day] = day.recipes }

            for (pair in planner) {
                // Skip adding UNPLANNED day header if no meals unplanned.
                if (pair.value.isEmpty() && pair.key == Day.UNPLANNED) {
                    continue
                }

                // Create a header for the day.
                items.add(RecipeListItem(pair.key.name.toLowerCase().capitalize()))

                // Create an item for each recipe planned for the day.
                pair.value.forEach { items.add(RecipeListItem(it, true)) }
            }

            MutableLiveData<List<RecipeListItem>>(items)
        }
    }

    fun onClearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            // Clear out the planner table.
            plannerRepository.deleteAll()
        }
    }

    fun onRemoveItem(recipe: Recipe, header: String?, offset: Int?) {
        // Something has gone wrong.
        if (header == null || offset == null) {
            Log.e(TAG, "Remove item failed: no header and/or offset.")
            return
        }

        // Update internal representation of the planner.
        updatePlanner()

        // Get the day from the heading.
        val day = Day.valueOf(header.toUpperCase())

        // Fetch list of recipes for a day in the internal representation.
        val dayList = planner[day]!!.toMutableList()
        dayList.removeAt(offset - 1)

        // Save new day list in internal representation.
        planner[day] = dayList

        // Save internal representation to the DB.
        savePlanner()
    }

    fun onListChange() {
        updatePlanner()
        savePlanner()
    }

    private fun updatePlanner() {
        var newMap = TreeMap<Day, List<RecipeWithIngredients>>()
        var lastHeading: String? = null
        var newList: ArrayList<RecipeWithIngredients> = ArrayList()

        // Map List<RecipeListItem> --> MutableMap<Day, RecipeWithIngredients>
        for (listItem in recipes.value!!) {
            if (listItem.viewType == RecipeListItem.VIEW_TYPE_HEADER) {
                if (lastHeading != null) {
                    newMap[Day.valueOf(lastHeading.toUpperCase())] = newList
                    newList = ArrayList()
                }
                lastHeading = listItem.heading
            } else {
                newList.add(listItem.recipeWithIngredients!!)
            }
        }

        if (lastHeading != null) newMap[Day.valueOf(lastHeading.toUpperCase())] = newList

        // Update the planner with the new one.
        planner = newMap
    }

    private fun savePlanner() {
        // Map from Map<Day, List<RecipeWithIngredient> --> List<PlannerDay>
        val data = planner.map { PlannerDay(it.key, it.value) }

        viewModelScope.launch(Dispatchers.IO) {
            plannerRepository.savePlannerDays(data)
        }
    }
}