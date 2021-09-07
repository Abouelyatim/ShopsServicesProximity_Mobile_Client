package com.smartcity.client.ui.main.flash_notification.state

import com.smartcity.client.ui.main.product.state.ProductStateEvent
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

    class ResolveUserAddressEvent: FlashStateEvent() {
        override fun errorInfo(): String {
            return "Resolve address attempt failed."
        }

        override fun toString(): String {
            return "ResolveUserAddressStateEvent"
        }
    }

    class SearchFlashDealsEvent(
        val date:String
    ): FlashStateEvent() {
        override fun errorInfo(): String {
            return "Search flashes attempt failed."
        }

        override fun toString(): String {
            return "SearchFlashDealsStateEvent"
        }
    }

    class SearchDiscountProductEvent: FlashStateEvent(){
        override fun errorInfo(): String {
            return "Search discounts attempt failed."
        }

        override fun toString(): String {
            return "SearchDiscountProductStateEvent"
        }
    }

    class SaveClickedProductEvent(
        val productId: Long
    ): FlashStateEvent(){
        override fun errorInfo(): String {
            return "Charge product attempt failed."
        }

        override fun toString(): String {
            return "SaveClickedProductStateEvent"
        }
    }

    class None: FlashStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}