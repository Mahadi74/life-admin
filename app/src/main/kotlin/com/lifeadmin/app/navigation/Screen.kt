package com.lifeadmin.app.navigation

/**
 * Navigation destinations for Life Admin
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object ItemDetails : Screen("item_details/{itemId}") {
        fun createRoute(itemId: Long) = "item_details/$itemId"
    }
    object AddItem : Screen("add_item")
    object EditItem : Screen("edit_item/{itemId}") {
        fun createRoute(itemId: Long) = "edit_item/$itemId"
    }
}
