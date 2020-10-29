package com.zacbrannelly.shoppingbuddy.ui.shoppinglist

import android.app.Application
import androidx.lifecycle.*
import com.zacbrannelly.shoppingbuddy.data.ShoppingListRepository
import com.zacbrannelly.shoppingbuddy.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    val listItems = MediatorLiveData<List<ShoppingListItem>>()

    private val recipeRepository: RecipeRepository
    private val plannerRepository: PlannerRepository
    private val shoppingListRepository: ShoppingListRepository
    private val shoppingList: LiveData<List<ShoppingList>>
    private val plannerDays: LiveData<List<PlannerDay>>

    init {
        val db = AppDatabase.getInstance(application)
        recipeRepository = RecipeRepository(db.recipeDao())
        plannerRepository = PlannerRepository(db.plannerDao())
        shoppingListRepository = ShoppingListRepository(db.shoppingListDao())

        shoppingList = shoppingListRepository.getShoppingList()
        plannerDays = plannerRepository.getPlannerDays()

        // Generate list items from the existing planner.
        listItems.addSource(plannerDays) { plannerDays ->
            listItems.value = generateFromPlanner(plannerDays)

            // Extract checked status from the shopping list table.
            listItems.addSource(shoppingList) { shoppingList ->
                // Get checked status of each ingredient in the shopping list table.
                listItems.value?.forEach { existingItem ->
                    val item = shoppingList.find { it.ingredientId == existingItem.ingredient.id }
                    existingItem.isChecked = item?.isChecked ?: false
                }

                // Trigger update
                listItems.value = listItems.value

                // Don't respond to updates in the shopping list table anymore.
                listItems.removeSource(this.shoppingList)
            }
        }
    }

    fun saveCurrentState() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = listItems.value?.map { item ->
                ShoppingList(item.ingredient.id, item.isChecked)
            }
            if (items != null) shoppingListRepository.clearAndInsertAll(items)
        }
    }

    private fun generateFromPlanner(plannerDays: List<PlannerDay>): List<ShoppingListItem> {
        val table: MutableMap<UUID, IngredientWithQty> = mutableMapOf()

        for (plannerDay in plannerDays) {
            for (recipe in plannerDay.recipes) {
                for (ingredientWithQty in recipe.ingredientsWithQty) {
                    if (table.containsKey(ingredientWithQty.ingredient.id)) {
                        table[ingredientWithQty.ingredient.id]!!.metadata.qty += ingredientWithQty.metadata.qty
                    } else {
                        table[ingredientWithQty.ingredient.id] = ingredientWithQty
                    }
                }
            }
        }

        return table.map { ig ->
            ShoppingListItem(ig.value.ingredient, ig.value.metadata.qty)
        }
    }
}