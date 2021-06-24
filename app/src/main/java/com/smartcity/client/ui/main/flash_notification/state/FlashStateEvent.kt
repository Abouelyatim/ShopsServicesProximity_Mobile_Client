package com.smartcity.client.ui.main.flash_notification.state

sealed class FlashStateEvent {

    class GetUserFlashDealsEvent: FlashStateEvent()

    class None: FlashStateEvent()
}