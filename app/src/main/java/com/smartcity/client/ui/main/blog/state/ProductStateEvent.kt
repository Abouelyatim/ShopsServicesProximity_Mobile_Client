package com.smartcity.client.ui.main.blog.state

sealed class ProductStateEvent {

    //class ProductSearchEvent : ProductStateEvent()

    class ProductMainEvent : ProductStateEvent()

    //class ProductSearchEvent :ProductStateEvent()

    class AddProductCartEvent(
        val variantId: Long,
        val quantity: Int
    ): ProductStateEvent()

    class None: ProductStateEvent()
}