package com.zacbrannelly.shoppingbuddy.ui

class ExpandableListItem(val icon: Int,
                         val header: String,
                         val items: List<Pair<String, String>> = emptyList(),
                         var expanded: Boolean = false)