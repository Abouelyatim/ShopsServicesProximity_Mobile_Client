package com.smartcity.client.ui.main.account.viewmodel

import com.smartcity.client.models.Address
import com.smartcity.client.models.Order
import com.smartcity.client.models.Store

fun AccountViewModel.getSelectedOrder(): Order? {
    getCurrentViewStateOrNew().let {
        return it.viewOrderFields.order
    }
}

fun AccountViewModel.getOrdersList(): List<Order> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.ordersList
    }
}
fun AccountViewModel.getOrderActionRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderActionRecyclerPosition
    }
}

fun AccountViewModel.getAddressList(): List<Address> {
    getCurrentViewStateOrNew().let {
        return it.addressList
    }
}

fun AccountViewModel.getOrderAction(): List<Triple<String,Int,Int>> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderAction
    }
}

fun AccountViewModel.getCenterLatitude(): Double {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.centerLatitude
    }
}

fun AccountViewModel.getCenterLongitude(): Double {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.centerLongitude
    }
}

fun AccountViewModel.getRadius(): Double {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.radius
    }
}

fun AccountViewModel.getStoresAround(): List<Store> {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.stores
    }
}