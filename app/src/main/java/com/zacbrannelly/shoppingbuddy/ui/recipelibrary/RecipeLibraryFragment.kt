package com.zacbrannelly.shoppingbuddy.ui.recipelibrary

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zacbrannelly.shoppingbuddy.R

class RecipeLibraryFragment : Fragment() {

    companion object {
        fun newInstance() = RecipeLibraryFragment()
    }

    private lateinit var viewModel: RecipeLibraryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_library, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RecipeLibraryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}