package com.zacbrannelly.shoppingbuddy.ui.recipelibrary

import android.app.ActivityOptions
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Pair
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.ui.RecipeListAdapter
import com.zacbrannelly.shoppingbuddy.ui.RecipeListItem
import com.zacbrannelly.shoppingbuddy.ui.detail.RecipeDetailActivity
import java.util.*

class RecipeLibraryFragment : Fragment() {

    companion object {
        fun newInstance() = RecipeLibraryFragment()
    }

    private lateinit var viewModel: RecipeLibraryViewModel
    private lateinit var recipeList: RecyclerView

    private var lastSharedImageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.fragment_recipe_library, container, false)

        recipeList = view.findViewById(R.id.recipe_list)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RecipeLibraryViewModel::class.java)

        val viewAdapter = RecipeListAdapter (requireContext())

        recipeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        // Notify the view model of list changes.
        viewAdapter.onItemClicked = { item, image -> onRecipeSelected(item, image) }
        viewAdapter.onItemRemoved = { recipe, _, _ -> viewModel.deleteRecipe(recipe) }

        viewModel.recipes.observe(viewLifecycleOwner) { items ->
            val listItems = items.map { recipe -> RecipeListItem(recipe, false) }
            viewAdapter.setItems(listItems)
        }
    }

    private fun onRecipeSelected(recipe: Recipe, image: ImageView) {
        // Setup shared element transition.
        val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), Pair(image, "imageView"))

        // Ensure last item selected has transition name reset.
        lastSharedImageView?.transitionName = null

        // Track the next image to be used in the animation.
        lastSharedImageView = image
        image.transitionName = "imageView"

        // Start the detail activity.
        startActivity(
            Intent(context, RecipeDetailActivity::class.java).apply {
                putExtra("recipe", recipe)
            },
            options.toBundle()
        )
    }

}