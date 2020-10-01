package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Pair
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.entity.Recipe
import com.zacbrannelly.shoppingbuddy.ui.RecipeListAdapter
import com.zacbrannelly.shoppingbuddy.ui.RecipeListItem
import com.zacbrannelly.shoppingbuddy.ui.detail.RecipeDetailActivity
import java.util.*

private const val TAG = "WeeklyPlannerFragment"

class WeeklyPlannerFragment : Fragment() {

    companion object {
        fun newInstance() = WeeklyPlannerFragment()
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

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WeeklyPlannerViewModel::class.java)

        val viewAdapter = RecipeListAdapter ({ item, image ->
            // Setup shared element transition.
            val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), Pair(image, "imageView"))

            // Ensure last item selected has transition name reset.
            lastSharedImageView?.transitionName = null

            // Track the next image to be used in the animation.
            lastSharedImageView = image
            image.transitionName = "imageView"

            // Start the detail activity.
            startActivity(
                Intent(context, RecipeDetailActivity::class.java),
                options.toBundle()
            )
        })

        viewModel.recipes.observe(viewLifecycleOwner) { recipeList ->
            viewAdapter.setItems(recipeList.map { item -> RecipeListItem(item, true) })
        }

        recipeList.isNestedScrollingEnabled = false

        recipeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
    }

}