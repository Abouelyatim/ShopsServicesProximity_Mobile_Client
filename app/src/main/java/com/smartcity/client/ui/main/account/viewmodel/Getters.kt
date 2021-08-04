package com.smartcity.client.ui.main.account.viewmodel

import com.smartcity.client.models.Address
import com.smartcity.client.models.Order
import com.smartcity.client.models.Store
import com.smartcity.client.models.UserInformation

fun AccountViewModel.getSelectedOrder(): Order? {
    getCurrentViewStateOrNew().let {
        return it.viewOrderFields.order
    }
}

fun AccountViewModel.getOrdersList(): List<Order> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.ordersList?:ArrayList<Order>()
    }
}
fun AccountViewModel.getOrderActionRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderActionRecyclerPosition?:0
    }
}

fun AccountViewModel.getAddressList(): List<Address> {
    getCurrentViewStateOrNew().let {
        return it.addressList?: listOf()
    }
}

fun AccountViewModel.getOrderAction(): List<Triple<String,Int,Int>> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderAction?: listOf()
    }
}

fun AccountViewModel.getCenterLatitude(): Double {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.centerLatitude?:0.0
    }
}

fun AccountViewModel.getCenterLongitude(): Double {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.centerLongitude?:0.0
    }
}

fun AccountViewModel.getRadius(): Double {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.radius?:20.0
    }
}

fun AccountViewModel.getStoresAround(): List<Store> {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.stores?: listOf()
    }
}

fun AccountViewModel.getUserInformation(): UserInformation? {
    getCurrentViewStateOrNew().let {
        return it.userInformation
    }
}