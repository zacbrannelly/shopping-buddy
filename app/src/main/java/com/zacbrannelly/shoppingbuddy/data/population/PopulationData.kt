package com.zacbrannelly.shoppingbuddy.data.population

import com.zacbrannelly.shoppingbuddy.data.FullRecipe
import com.zacbrannelly.shoppingbuddy.data.Planner

data class PopulationData(
    val recipes: List<FullRecipe>,
    val planners: List<Planner>
)