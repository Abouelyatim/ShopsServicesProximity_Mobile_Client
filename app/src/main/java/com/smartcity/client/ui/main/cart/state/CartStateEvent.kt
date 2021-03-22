package com.smartcity.client.ui.main.cart.state

sealed class CartStateEvent {

     class GetUserCart():CartStateEvent()

    class AddProductCartEvent(
        val variantId: Long,
        val quantity: Int
    ): CartStateEvent()

    class None: CartStateEvent()
}