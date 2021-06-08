package com.smartcity.client.ui.main.account.viewmodel

import com.smartcity.client.models.Address
import com.smartcity.client.models.Order

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