package com.smartcity.client.ui

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
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

interface AreYouSureCallback {

    fun proceed()

    fun cancel()
}