package com.zacbrannelly.shoppingbuddy.ui.shoppinglist

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R

class ShoppingListFragment : Fragment() {

    companion object {
        fun newInstance() = ShoppingListFragment()
    }

    private lateinit var shoppingList: RecyclerView
    private lateinit var viewModel: ShoppingListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_shopping_list, container, false).also {
            shoppingList = it.findViewById(R.id.shopping_list)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ShoppingListViewModel::class.java)

        shoppingList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ShoppingListAdapter().also { adapter ->
                // Observe changes to the list view form the view model.
                viewModel.listItems.observe(viewLifecycleOwner) { items ->
                    adapter.setItems(items)
                }

                // Trigger view model to save state to the DB.
                adapter.onItemSelected = { ingredient, checked ->
                    viewModel.updateItem(ingredient, checked)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        // Disable the clear all action.
        menu.findItem(R.id.action_clear_all).isVisible = false

        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        // Listen for search queries, notify the view model to filter the list based on the query.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.queryIngredients(query)
                return false
            }
        })
    }

}