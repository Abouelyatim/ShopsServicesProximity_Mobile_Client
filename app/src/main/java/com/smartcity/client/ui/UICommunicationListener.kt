package com.smartcity.client.ui


interface UICommunicationListener {

    fun onUIMessageReceived(uiMessage: UIMessage)
}