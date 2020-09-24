package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.entity.Recipe
import com.zacbrannelly.shoppingbuddy.ui.RecipeListAdapter
import com.zacbrannelly.shoppingbuddy.ui.RecipeListItem
import java.util.*

class WeeklyPlannerFragment : Fragment() {

    companion object {
        fun newInstance() = WeeklyPlannerFragment()
    }

    private lateinit var viewModel: WeeklyPlannerViewModel
    private lateinit var recipeList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.fragment_weekly_planner, container, false)

        recipeList = view.findViewById(R.id.recipe_list)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WeeklyPlannerViewModel::class.java)

        val viewAdapter = RecipeListAdapter()
        val mockItems = listOf(
            RecipeListItem("Monday"),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem("Tuesday"),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem("Wednesday"),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem("Thursday"),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem("Friday"),
            RecipeListItem(Recipe(UUID.randomUUID()), true),
            RecipeListItem(Recipe(UUID.randomUUID()), true)
        )

        viewAdapter.setItems(mockItems)

        recipeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
    }

}