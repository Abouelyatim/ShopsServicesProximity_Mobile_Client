package com.smartcity.client.ui.main.store.state

sealed class StoreStateEvent{

    class CustomCategoryMain : StoreStateEvent()

    class ProductMain(
        val id: Long
    ) : StoreStateEvent()

    class AllProduct() : StoreStateEvent()

    class None: StoreStateEvent()
}