package com.zacbrannelly.shoppingbuddy.ui.form

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R

class RecipeFormActivity: AppCompatActivity() {

    private lateinit var viewModel: RecipeFormViewModel
    private lateinit var ingredientsList: RecyclerView
    private lateinit var stepsList: RecyclerView
    private lateinit var selectImageButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_form)
        viewModel = ViewModelProviders.of(this).get(RecipeFormViewModel::class.java)

        selectImageButton = findViewById(R.id.select_image_button)
        selectImageButton.setOnClickListener {

        }

        ingredientsList = findViewById(R.id.ingredients_list)
        ingredientsList.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = TextInputListAdapter(
                R.layout.list_ingredient_input_item,
                listOf(R.id.ingredient_field, R.id.ingredient_qty_field)
            )
        }

        stepsList = findViewById(R.id.steps_list)
        stepsList.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = TextInputListAdapter(
                R.layout.list_step_input_item,
                listOf(R.id.step_field)
            )
        }
    }
}