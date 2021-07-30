package com.smartcity.client.ui.deleted

import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import com.smartcity.client.ui.deleted.DataState

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