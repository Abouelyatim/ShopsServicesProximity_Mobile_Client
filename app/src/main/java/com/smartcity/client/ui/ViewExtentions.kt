package com.smartcity.client.ui

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.smartcity.client.R
import com.smartcity.client.util.StateMessageCallback

private val TAG: String = "AppDebug"

fun Activity.displayToast(
    @StringRes message:Int,
    stateMessageCallback: StateMessageCallback
){
    Toast.makeText(this, message,Toast.LENGTH_LONG).show()
    stateMessageCallback.removeMessageFromStack()
}

fun Activity.displayToast(
    message:String,
    stateMessageCallback: StateMessageCallback
){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    stateMessageCallback.removeMessageFromStack()
}

fun Activity.displaySnackBar(
    message:String,
    stateMessageCallback: StateMessageCallback
){
    val snckBar= Snackbar.make(this.window.decorView.findViewById(android.R.id.content),message,
        Snackbar.LENGTH_SHORT)
    snckBar.setAnchorView(this.findViewById(R.id.bottom_navigation_view) as View)
    snckBar.show()
    stateMessageCallback.removeMessageFromStack()
}

interface AreYouSureCallback {

    fun proceed()

    fun cancel()
}