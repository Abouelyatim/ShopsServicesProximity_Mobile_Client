package com.smartcity.client.ui.main.flash_notification.state

import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.util.StateEvent

sealed class FlashStateEvent: StateEvent {

    class GetUserFlashDealsEvent(
        val date:String
    ): FlashStateEvent() {
        override fun errorInfo(): String {
            return "Get flashes attempt failed."
        }

        override fun toString(): String {
            return "GetUserFlashDealsStateEvent"
        }
    }

    class GetUserDiscountProductEvent: FlashStateEvent(){
        override fun errorInfo(): String {
            return "Get discounts attempt failed."
        }

        override fun toString(): String {
            return "GetUserDiscountProductStateEvent"
        }
    }

    class AddProductCartEvent(
        val variantId: Long,
        val quantity: Int
    ): FlashStateEvent(){
        override fun errorInfo(): String {
            return "Add product to cart attempt failed."
        }

        override fun toString(): String {
            return "AddProductCartStateEvent"
        }
    }

    class GetUserDefaultCityEvent: FlashStateEvent(){
        override fun errorInfo(): String {
            return "Get default city attempt failed."
        }

        override fun toString(): String {
            return "GetUserDefaultCityStateEvent"
        }
    }

    class None: FlashStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}