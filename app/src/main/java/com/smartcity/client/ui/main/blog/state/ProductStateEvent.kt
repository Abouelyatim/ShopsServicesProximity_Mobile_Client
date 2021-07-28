package com.smartcity.client.ui.main.blog.state

sealed class ProductStateEvent {

    class ProductMainEvent : ProductStateEvent()

    class ProductSearchEvent : ProductStateEvent()

    class AddProductCartEvent(
        val variantId: Long,
        val quantity: Int
    ): ProductStateEvent()

    class GetStoreCustomCategoryEvent(
        val storeId: Long
    ): ProductStateEvent()

    class GetProductsByCustomCategoryEvent(
        val customCategoryId: Long
    ): ProductStateEvent()

    class GetAllStoreProductsEvent(
        val storeId: Long
    ): ProductStateEvent()

    class FollowStoreEvent(
        val storeId: Long
    ): ProductStateEvent()

    class StopFollowingStoreEvent(
        val storeId: Long
    ): ProductStateEvent()

    class IsFollowingStoreEvent(
        val storeId: Long
    ): ProductStateEvent()

    class ProductLayoutChangeEvent(): ProductStateEvent()

    class BackClickedEvent(
        val tag:String
    ): ProductStateEvent()

    class SearchEvent(
        val tag:String
    ): ProductStateEvent()

    class None: ProductStateEvent()
}