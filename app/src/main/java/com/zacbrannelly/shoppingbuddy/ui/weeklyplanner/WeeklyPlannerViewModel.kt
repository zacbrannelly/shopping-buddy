package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.data.RecipeWithIngredients
import com.zacbrannelly.shoppingbuddy.ui.RecipeListItem
import kotlin.collections.ArrayList

private const val TAG = "WeeklyPlannerViewModel"

class WeeklyPlannerViewModel : ViewModel() {
    var recipes = MutableLiveData<List<RecipeListItem>>()

    var planner = mutableMapOf<String, List<RecipeWithIngredients>>(
        "Monday"    to emptyList(),
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
        var newMap = mutableMapOf<String, List<RecipeWithIngredients>>()
        var lastHeading: String? = null
        var newList: ArrayList<RecipeWithIngredients> = ArrayList()

        for (listItem in recipes.value!!) {
            if (listItem.viewType == RecipeListItem.VIEW_TYPE_HEADER) {
                if (lastHeading != null) {
                    newMap[lastHeading] = newList
                    newList = ArrayList()
                }
                lastHeading = listItem.heading
            } else {
                newList.add(listItem.recipeWithIngredients!!)
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