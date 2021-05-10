package com.smartcity.client.ui.main.cart.state

import com.smartcity.client.models.Address
import com.smartcity.client.models.BillTotal
import com.smartcity.client.models.Order

sealed class CartStateEvent {

     class GetUserCart():CartStateEvent()

    class AddProductCartEvent(
        val variantId: Long,
        val quantity: Int
    ): CartStateEvent()

    class DeleteProductCartEvent(
        val variantId: Long
    ): CartStateEvent()

    class PlaceOrderEvent(
        val order: Order
    ): CartStateEvent()

    class GetStorePolicy(
        val storeId:Long
    ):CartStateEvent()

    class GetTotalBill(
        val bill:BillTotal
    ):CartStateEvent()

    class GetUserAddresses(): CartStateEvent()

    class SaveAddress(
        var address: Address
    ):CartStateEvent()

    class GetUserInformation(
    ):CartStateEvent()

    class None: CartStateEvent()
}