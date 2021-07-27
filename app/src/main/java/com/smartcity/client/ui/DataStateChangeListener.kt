package com.smartcity.client.ui

import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

interface DataStateChangeListener{

    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()

    fun displayBadgeBottomNavigationFlash(bool: Boolean)

    fun displayBottomNavigation(bool: Boolean)

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean

    fun isFineLocationPermissionGranted(): Boolean

    fun displayAppBar(bool: Boolean)

    fun setAppBarLayout(fragment: Fragment)

    fun updateStatusBarColor(@ColorRes statusBarColor: Int, statusBarTextColor:Boolean)
}