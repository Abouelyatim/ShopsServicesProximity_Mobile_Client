package com.smartcity.client.ui.main.cart.state

import com.smartcity.client.models.Address
import com.smartcity.client.models.Bill
import com.smartcity.client.models.product.Cart
import com.smartcity.client.ui.main.account.state.AccountStateEvent

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
        val cart:Cart?
    ): CartStateEvent()

    class GetStorePolicy(
        val storeId:Long
    ):CartStateEvent()

    class GetTotalBill(
        val bill:Bill
    ):CartStateEvent()

    class GetUserAddresses(): CartStateEvent()

    class SaveAddress(
        var address: Address
    ):CartStateEvent()

    class None: CartStateEvent()
}