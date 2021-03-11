package com.smartcity.client.ui.main.blog.state

sealed class ProductStateEvent {

    //class ProductSearchEvent : ProductStateEvent()

    class ProductMainEvent : ProductStateEvent()

    //class ProductSearchEvent :ProductStateEvent()

    class None: ProductStateEvent()
}