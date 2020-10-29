package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zacbrannelly.shoppingbuddy.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope

class SelectRecipeActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SelectRecipeViewModel
    private lateinit var doneButton: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_recipe)

        // Get the view model instance.
        viewModel = ViewModelProviders.of(this).get(SelectRecipeViewModel::class.java)

        // Setup toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Show back button in toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        doneButton = findViewById(R.id.done_button)
        doneButton.setOnClickListener {
            viewModel.onConfirm()
            finish()
        }

        val viewAdapter = SelectRecipeListAdapter(baseContext)

        viewAdapter.onItemSelected = { recipe -> viewModel.onRecipeSelected(recipe) }

        viewModel.recipes.observe(this) {
            viewAdapter.setItems(it)
        }

        viewModel.selectedRecipes.observe(this) {
            viewAdapter.setSelectedItems(it)

            // Show done button if item's are selected.
            doneButton.visibility = if (it.size > 0) View.VISIBLE else View.GONE
            doneButton.text =
                if (it.size == 1)
                    baseContext.getString(R.string.fab_select_recipe_singular)
                else
                    baseContext.getString(R.string.fab_select_recipe_plural).format(it.size)
        }

        recyclerView = findViewById(R.id.select_recipe_list)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_select_recipe, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                // Notify view model when search has been made.
                viewModel.onRecipeSearch(query)
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}