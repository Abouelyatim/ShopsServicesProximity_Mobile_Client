package com.smartcity.client.ui.main.product.state

import com.smartcity.client.util.StateEvent

sealed class ProductStateEvent: StateEvent {

    class ProductMainEvent : ProductStateEvent() {
        override fun errorInfo(): String {
            return "Get products attempt failed."
        }

        override fun toString(): String {
            return "ProductMainStateEvent"
        }
    }

    class ProductSearchEvent : ProductStateEvent(){
        override fun errorInfo(): String {
            return "Search products attempt failed."
        }

        override fun toString(): String {
            return "ProductSearchStateEvent"
        }
    }

    class ProductInterestEvent : ProductStateEvent(){
        override fun errorInfo(): String {
            return "Get products attempt failed."
        }

        override fun toString(): String {
            return "ProductInterestStateEvent"
        }
    }

    class AddProductCartEvent(
        val variantId: Long,
        val quantity: Int
    ): ProductStateEvent(){
        override fun errorInfo(): String {
            return "Add product to cart attempt failed."
        }

        override fun toString(): String {
            return "AddProductCartStateEvent"
        }
    }

    class GetStoreCustomCategoryEvent(
        val storeId: Long
    ): ProductStateEvent(){
        override fun errorInfo(): String {
            return "Get store information attempt failed."
        }

        override fun toString(): String {
            return "GetStoreCustomCategoryStateEvent"
        }
    }

    class GetProductsByCustomCategoryEvent(
        val customCategoryId: Long
    ): ProductStateEvent(){
        override fun errorInfo(): String {
            return "Get products attempt failed."
        }

        override fun toString(): String {
            return "GetProductsByCustomCategoryStateEvent"
        }
    }

    class GetAllStoreProductsEvent(
        val storeId: Long
    ): ProductStateEvent(){
        override fun errorInfo(): String {
            return "Get products attempt failed."
        }

        override fun toString(): String {
            return "GetAllStoreProductsStateEvent"
        }
    }

    class FollowStoreEvent(
        val storeId: Long
    ): ProductStateEvent(){
        override fun errorInfo(): String {
            return "Follow store attempt failed."
        }

        override fun toString(): String {
            return "FollowStoreStateEvent"
        }
    }

    class StopFollowingStoreEvent(
        val storeId: Long
    ): ProductStateEvent(){
        override fun errorInfo(): String {
            return "Stop following store attempt failed."
        }

        override fun toString(): String {
            return "StopFollowingStoreStateEvent"
        }
    }

    class IsFollowingStoreEvent(
        val storeId: Long
    ): ProductStateEvent(){
        override fun errorInfo(): String {
            return "Follow store attempt failed."
        }

        override fun toString(): String {
            return "IsFollowingStoreStateEvent"
        }
    }

    class None: ProductStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}