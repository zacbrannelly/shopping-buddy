package com.zacbrannelly.shoppingbuddy.ui.shoppinglist

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
                adapter.onItemSelected = { viewModel.saveCurrentState() }
            }
        }
    }

}