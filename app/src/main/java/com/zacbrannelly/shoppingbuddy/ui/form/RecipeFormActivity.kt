package com.zacbrannelly.shoppingbuddy.ui.form

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.FullRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RecipeFormActivity: AppCompatActivity() {

    private lateinit var viewModel: RecipeFormViewModel
    private lateinit var nameField: TextInputEditText
    private lateinit var nameFieldLayout: TextInputLayout
    private lateinit var typeField: TextInputEditText
    private lateinit var typeFieldLayout: TextInputLayout
    private lateinit var ingredientsList: RecyclerView
    private lateinit var ingredientsListAdapter: TextInputListAdapter
    private lateinit var stepsList: RecyclerView
    private lateinit var stepsListAdapter: TextInputListAdapter
    private lateinit var selectImageButton: View
    private lateinit var image: ImageView
    private lateinit var saveButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_form)
        viewModel = ViewModelProviders.of(this).get(RecipeFormViewModel::class.java)

        image = findViewById(R.id.app_bar_image)
        nameField = findViewById(R.id.recipe_name_field)
        nameFieldLayout = findViewById(R.id.recipe_name_field_layout)
        typeField = findViewById(R.id.recipe_type_field)
        typeFieldLayout = findViewById(R.id.recipe_type_field_layout)

        selectImageButton = findViewById(R.id.select_image_button)
        selectImageButton.setOnClickListener {
            ImageOptionsDialogFragment(
                { takeImage() },
                { selectImageFromGallery() }
            ).show(supportFragmentManager, "options")
        }

        ingredientsListAdapter = TextInputListAdapter(
            R.layout.list_ingredient_input_item,
            listOf(R.id.ingredient_field, R.id.ingredient_qty_field)
        )

        ingredientsList = findViewById(R.id.ingredients_list)
        ingredientsList.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsListAdapter
        }

        stepsListAdapter = TextInputListAdapter(
            R.layout.list_step_input_item,
            listOf(R.id.step_field)
        )

        stepsList = findViewById(R.id.steps_list)
        stepsList.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = stepsListAdapter
        }

        saveButton = findViewById(R.id.save_button)
        saveButton.setOnClickListener { onSave() }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Show back button in app bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // If editing an existing recipe, load this recipe into the form.
        val recipe = intent.getParcelableExtra<FullRecipe>("recipe")
        if (recipe != null) viewModel.loadRecipe(recipe)

        viewModel.recipe.observe(this) {
            // Load the image of the recipe.
            lifecycleScope.launch(Dispatchers.Main) {
                val task = async(Dispatchers.IO) { it.recipe.loadBitmap(baseContext) }
                val bitmap = task.await()
                image.setImageBitmap(bitmap)
            }

            // Set the name and type fields.
            nameField.setText(it.recipe.name)
            typeField.setText(it.recipe.type)

            // Set the ingredient fields.
            ingredientsListAdapter.setFields(it.ingredients.map { i ->
                listOf(
                    i.ingredient.name,
                    "${i.metadata.qty} ${i.ingredient.units}"
                )
            })

            // Set the steps fields.
            stepsListAdapter.setFields(it.steps.map { s ->
                listOf(s.description)
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true;
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result from take image request
        if (requestCode == TAKE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            image.setImageBitmap(bitmap)

            // Notify the view model of the new bitmap.
            viewModel.onBitmapLoaded(bitmap)
        }

        // Result from gallery request.
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            // Load image into the app bar image.
            val imageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            image.setImageBitmap(bitmap)

            // Notify the view model of the new bitmap.
            viewModel.onBitmapLoaded(bitmap)
        }
    }

    private fun takeImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, TAKE_IMAGE_REQUEST)
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST)
    }

    private fun validateFields(): Boolean {
        // Reset all errors.
        nameFieldLayout.error = null
        nameFieldLayout.isErrorEnabled = false
        typeFieldLayout.error = null
        typeFieldLayout.isErrorEnabled = false
        ingredientsListAdapter.clearErrors()

        val requiredFieldMsg = resources.getString(R.string.required_field)

        // Name is a required field.
        if (nameField.text.isNullOrBlank()) {
            nameFieldLayout.error = requiredFieldMsg
            return false
        }

        // Type is a required field.
        if (typeField.text.isNullOrBlank()) {
            typeFieldLayout.error = requiredFieldMsg
            return false
        }

        // Validate ingredient fields
        return validateIngredientFields()
    }

    private fun validateIngredientFields(): Boolean {
        val requiredFieldMsg = resources.getString(R.string.required_field)

        // At least one ingredient is required.
        if (ingredientsListAdapter.getFields().isEmpty()) {
            // Show error on row in ingredients inputs list.
            ingredientsListAdapter.setErrorsAt(0, listOf(
                requiredFieldMsg,
                requiredFieldMsg
            ))
            return false
        }

        // Format example: "23 cups".
        var result = true
        val unitRegex = Regex("^\\d+\\.?(\\d+)? [^ ]+$")

        // Check each ingredient field is valid.
        ingredientsListAdapter.getFields().forEachIndexed { i, values ->
            var nameError: String? = null
            var unitsError: String? = null

            // If name field is blank
            if (values[0].isBlank()) {
                nameError = requiredFieldMsg
            }

            // If unit field is blank
            if (values[1].isBlank()) {
                unitsError = requiredFieldMsg
            } else if (!unitRegex.matches(values[1])) {
                // If unit field is the wrong format, show example in error field.
                unitsError = resources.getString(R.string.invalid_units)
            }

            // If an error occurred, show it and signal failure.
            if (nameError != null || unitsError != null) {
                ingredientsListAdapter.setErrorsAt(i, listOf(nameError, unitsError))
                result = false
            }
        }

        return result
    }

    private fun onSave() {
        if (!validateFields()) return

        // Save the new information to the DB.
        viewModel.onSave(
            nameField.text.toString(),
            typeField.text.toString(),
            ingredientsListAdapter.getFields(),
            stepsListAdapter.getFields()
        )

        // Close the activity.
        finish()
    }

    companion object {
        private const val TAG = "RecipeFormActivity"
        private const val TAKE_IMAGE_REQUEST = 1
        private const val GALLERY_REQUEST = 2
    }
}