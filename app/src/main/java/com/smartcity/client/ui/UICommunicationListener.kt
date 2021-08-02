package com.smartcity.client.ui

import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import com.smartcity.client.util.Response
import com.smartcity.client.util.StateMessageCallback

interface UICommunicationListener {

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

    fun displayProgressBar(isLoading: Boolean)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean

    fun displayBadgeBottomNavigationFlash(bool: Boolean)

    fun displayBottomNavigation(bool: Boolean)

    fun isFineLocationPermissionGranted(): Boolean

    fun displayAppBar(bool: Boolean)

    fun setAppBarLayout(fragment: Fragment)

    fun updateStatusBarColor(@ColorRes statusBarColor: Int, statusBarTextColor:Boolean)
}