package com.zacbrannelly.shoppingbuddy.ui.recipelibrary

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.ui.RecipeListAdapter
import com.zacbrannelly.shoppingbuddy.ui.RecipeListItem
import java.util.*

class RecipeLibraryFragment : Fragment() {

    companion object {
        fun newInstance() = RecipeLibraryFragment()
    }

    private lateinit var viewModel: RecipeLibraryViewModel
    private lateinit var recipeList: RecyclerView

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

        val viewAdapter = RecipeListAdapter (requireContext(), { _, _ ->

        })

        recipeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        viewModel.recipes.observe(viewLifecycleOwner) { items ->
            val listItems = items.map { recipe -> RecipeListItem(recipe, false) }
            viewAdapter.setItems(listItems)
        }
    }

}