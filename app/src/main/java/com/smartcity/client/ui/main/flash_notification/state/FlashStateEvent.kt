package com.smartcity.client.ui.main.flash_notification.state

sealed class FlashStateEvent {

    class GetUserFlashDealsEvent: FlashStateEvent()

    class GetUserDiscountProductEvent: FlashStateEvent()

    class AddProductCartEvent(
        val variantId: Long,
        val quantity: Int
    ): FlashStateEvent()

    class None: FlashStateEvent()
}