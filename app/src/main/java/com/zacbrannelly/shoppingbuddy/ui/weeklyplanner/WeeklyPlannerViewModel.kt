package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zacbrannelly.shoppingbuddy.entity.Recipe
import java.util.*

class WeeklyPlannerViewModel : ViewModel() {
    var recipes = MutableLiveData<List<Recipe>>(listOf(
        Recipe(UUID.randomUUID()),
        Recipe(UUID.randomUUID()),
        Recipe(UUID.randomUUID()),
        Recipe(UUID.randomUUID()),
        Recipe(UUID.randomUUID()),
        Recipe(UUID.randomUUID())
    ))
}