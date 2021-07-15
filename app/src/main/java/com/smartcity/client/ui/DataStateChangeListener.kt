package com.smartcity.client.ui

interface DataStateChangeListener{

    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()

    fun displayBadgeBottomNavigationFlash(bool: Boolean)

    fun displayBottomNavigation(bool: Boolean)

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean

    fun isFineLocationPermissionGranted(): Boolean
}