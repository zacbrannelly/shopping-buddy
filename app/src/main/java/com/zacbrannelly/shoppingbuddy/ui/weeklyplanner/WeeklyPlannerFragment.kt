package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.data.RecipeWithIngredients
import com.zacbrannelly.shoppingbuddy.ui.RecipeListAdapter
import com.zacbrannelly.shoppingbuddy.ui.detail.RecipeDetailActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeeklyPlannerFragment : Fragment() {

    companion object {
        private const val TAG = "WeeklyPlannerFragment"
        private const val SELECT_RECIPE_REQUEST = 1
    }

    private lateinit var viewModel: WeeklyPlannerViewModel
    private lateinit var recipeList: RecyclerView
    private var lastSharedImageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.fragment_weekly_planner, container, false)

        recipeList = view.findViewById(R.id.recipe_list)

        view.findViewById<FloatingActionButton>(R.id.add_button).setOnClickListener {
            // Open the Select Recipe Fragment.
            startActivityForResult(
                Intent(context, SelectRecipeActivity::class.java),
                SELECT_RECIPE_REQUEST
            )
        }

        setHasOptionsMenu(true)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WeeklyPlannerViewModel::class.java)

        val viewAdapter = RecipeListAdapter(requireContext())

        // Notify the view model of list changes
        viewAdapter.onItemClicked = { recipe, imageView -> onRecipeSelected(recipe, imageView) }
        viewAdapter.onItemOrderChanged = { viewModel.onListChange() }
        viewAdapter.onItemRemoved = { recipe, heading, offset -> viewModel.onRemoveItem(recipe, heading, offset) }

        // Observe the recipe list generated by the view model.
        viewModel.recipes.observe(viewLifecycleOwner) { recipeList ->
            viewAdapter.setItems(recipeList)
        }

        recipeList.isNestedScrollingEnabled = false

        recipeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        // Hide the search action
        menu.findItem(R.id.app_bar_search).isVisible = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If coming back from adding recipes to the planner, scroll to the top.
        if (requestCode == SELECT_RECIPE_REQUEST && resultCode == Activity.RESULT_OK) {
            viewModel.viewModelScope.launch {
                // Wait some time so the items can be added to the top of the list.
                delay(500)

                // Smoothly scroll to the top of the planner.
                recipeList.smoothScrollToPosition(0)
            }
        }
    }

    private fun onRecipeSelected(item: Recipe, image: ImageView) {
        // Setup shared element transition.
        val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), Pair(image, "imageView"))

        // Ensure last item selected has transition name reset.
        lastSharedImageView?.transitionName = null

        // Track the next image to be used in the animation.
        lastSharedImageView = image
        image.transitionName = "imageView"

        // Start the detail activity.
        startActivity(
            Intent(context, RecipeDetailActivity::class.java).apply { putExtra("recipe", item) },
            options.toBundle()
        )
    }

}