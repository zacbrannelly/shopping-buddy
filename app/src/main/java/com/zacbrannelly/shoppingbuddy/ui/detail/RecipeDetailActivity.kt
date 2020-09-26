package com.zacbrannelly.shoppingbuddy.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.ui.ExpandableListAdapter
import com.zacbrannelly.shoppingbuddy.ui.ExpandableListItem

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var expandableList: RecyclerView
    private lateinit var viewModel: RecipeDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        viewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel::class.java)

        expandableList = findViewById(R.id.expandable_list)
        expandableList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ExpandableListAdapter(context, listOf(
                ExpandableListItem(R.drawable.ic_format_list_bulleted, "Ingredients"),
                ExpandableListItem(R.drawable.ic_outdoor_grill, "Steps")
            ))
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Show back button in app bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // If they press the back button in the app bar, navigate backwards.
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}