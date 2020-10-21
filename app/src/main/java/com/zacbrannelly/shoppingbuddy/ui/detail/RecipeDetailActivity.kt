package com.zacbrannelly.shoppingbuddy.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.Recipe
import com.zacbrannelly.shoppingbuddy.ui.ExpandableListAdapter
import com.zacbrannelly.shoppingbuddy.ui.ExpandableListItem
import com.zacbrannelly.shoppingbuddy.ui.form.RecipeFormActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var expandableList: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var heading: TextView
    private lateinit var caption: TextView
    private lateinit var editButton: FloatingActionButton
    private lateinit var viewModel: RecipeDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        imageView = findViewById(R.id.app_bar_image)
        heading = findViewById(R.id.recipe_detail_heading)
        caption = findViewById(R.id.recipe_detail_caption)
        editButton = findViewById(R.id.edit_button)

        editButton.setOnClickListener { onEditClicked() }

        viewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel::class.java)

        // Retrieve recipe information when available.
        viewModel.recipe.observe(this) {
            heading.text = it.recipe.name
            caption.text = it.recipe.type
        }

        // Retrieve recipe image when available.
        viewModel.recipeImage.observe(this) {
            imageView.setImageBitmap(it)
        }

        // Populate detail view with recipe
        val recipe = intent.getParcelableExtra<Recipe>("recipe")
        if (recipe != null) viewModel.loadRecipe(recipe)

        val listAdapter = ExpandableListAdapter(
            applicationContext,
            listOf(
                ExpandableListItem(R.drawable.ic_format_list_bulleted, "Ingredients"),
                ExpandableListItem(R.drawable.ic_outdoor_grill, "Steps")
            )
        )

        viewModel.expandableListItems.observe(this) {
            // Update expandable list
            listAdapter.setItems(it)
        }

        expandableList = findViewById(R.id.expandable_list)
        expandableList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
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

    private fun onEditClicked() {
        val intent = Intent(baseContext, RecipeFormActivity::class.java).apply {
            putExtra("recipe", viewModel.recipe.value)
        }
        startActivity(intent)
    }
}