package com.smartcity.client.ui.main.cart.state

import com.smartcity.client.models.product.Cart

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

    class None: CartStateEvent()
}