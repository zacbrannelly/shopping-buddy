package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zacbrannelly.shoppingbuddy.R

class WeeklyPlannerFragment : Fragment() {

    companion object {
        fun newInstance() = WeeklyPlannerFragment()
    }

    private lateinit var viewModel: WeeklyPlannerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weekly_planner, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WeeklyPlannerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}