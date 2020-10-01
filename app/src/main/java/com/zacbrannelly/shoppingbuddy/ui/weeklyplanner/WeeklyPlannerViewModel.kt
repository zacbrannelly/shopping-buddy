package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zacbrannelly.shoppingbuddy.entity.Recipe
import com.zacbrannelly.shoppingbuddy.ui.RecipeListItem
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "WeeklyPlannerViewModel"

class WeeklyPlannerViewModel : ViewModel() {
    var recipes = MutableLiveData<List<RecipeListItem>>()

    var planner = mutableMapOf<String, List<Recipe>>(
        "Monday"    to listOf(Recipe(UUID.randomUUID()), Recipe(UUID.randomUUID())),
        "Tuesday"   to emptyList(),
        "Wednesday" to emptyList(),
        "Thursday"  to emptyList(),
        "Friday"    to emptyList(),
        "Saturday"  to emptyList(),
        "Sunday"    to emptyList()
    )

    init {
        generateListItems()
    }

    fun onListOrderChange() {
        updatePlanner()
    }

    private fun updatePlanner() {
        var newMap = mutableMapOf<String, List<Recipe>>()
        var lastHeading: String? = null
        var newList: ArrayList<Recipe> = ArrayList()

        for (listItem in recipes.value!!) {
            if (listItem.viewType == RecipeListItem.VIEW_TYPE_HEADER) {
                if (lastHeading != null) {
                    newMap[lastHeading] = newList
                    newList = ArrayList()
                }
                lastHeading = listItem.heading
            } else {
                newList.add(listItem.recipe!!)
            }
        }

        if (lastHeading != null) newMap[lastHeading] = newList

        // Update the planner with the new one.
        planner = newMap
    }

    private fun generateListItems() {
        val items = ArrayList<RecipeListItem>()

        for (day in planner) {
            items.add(RecipeListItem(day.key))
            for (item in day.value) {
                items.add(RecipeListItem(item, true))
            }
        }

        recipes.value = items
    }
}