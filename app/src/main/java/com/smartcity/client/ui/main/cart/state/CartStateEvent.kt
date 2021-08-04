package com.smartcity.client.ui.main.cart.state

import com.smartcity.client.models.Address
import com.smartcity.client.models.BillTotal
import com.smartcity.client.models.Order
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.util.StateEvent

sealed class CartStateEvent: StateEvent {

     class GetUserCartEvent():CartStateEvent() {
         override fun errorInfo(): String {
             return "Get cart attempt failed."
         }

         override fun toString(): String {
             return "GetUserCartStateEvent"
         }
     }

    class AddProductCartEvent(
        val variantId: Long,
        val quantity: Int
    ): CartStateEvent(){
         override fun errorInfo(): String {
             return "Add product to cart attempt failed."
         }

         override fun toString(): String {
             return "AddProductCartStateEvent"
         }
    }

    class DeleteProductCartEvent(
        val variantId: Long
    ): CartStateEvent(){
         override fun errorInfo(): String {
             return "Delete product from cart attempt failed."
         }

         override fun toString(): String {
             return "DeleteProductCartStateEvent"
         }
    }

    class PlaceOrderEvent(
        val order: Order
    ): CartStateEvent(){
         override fun errorInfo(): String {
             return "Place order attempt failed."
         }

         override fun toString(): String {
             return "PlaceOrderStateEvent"
         }
    }

    class GetStorePolicyEvent(
        val storeId:Long
    ):CartStateEvent(){
         override fun errorInfo(): String {
             return "Get store policy attempt failed."
         }

         override fun toString(): String {
             return "GetStorePolicyStateEvent"
         }
    }

    class GetTotalBillEvent(
        val bill:BillTotal
    ):CartStateEvent(){
         override fun errorInfo(): String {
             return "Get total price attempt failed."
         }

         override fun toString(): String {
             return "GetTotalBillStateEvent"
         }
    }

    class GetUserAddressesEvent(): CartStateEvent(){
         override fun errorInfo(): String {
             return "Get address attempt failed."
         }

         override fun toString(): String {
             return "GetUserAddressesStateEvent"
         }
    }

    class SaveAddressEvent(
        var address: Address
    ):CartStateEvent(){
         override fun errorInfo(): String {
             return "Save address attempt failed."
         }

         override fun toString(): String {
             return "SaveAddressStateEvent"
         }
    }

    class GetUserInformationEvent(
    ):CartStateEvent(){
         override fun errorInfo(): String {
             return "Get information attempt failed."
         }

         override fun toString(): String {
             return "GetUserInformationStateEvent"
         }
    }

    class GetUserDefaultCityEvent: CartStateEvent(){
        override fun errorInfo(): String {
            return "Get default city attempt failed."
        }

        override fun toString(): String {
            return "GetUserDefaultCityStateEvent"
        }
    }

    class None: CartStateEvent(){
         override fun errorInfo(): String {
             return "None"
         }
    }
}