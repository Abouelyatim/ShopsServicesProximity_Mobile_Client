package com.smartcity.client.ui.main.account.viewmodel

import com.smartcity.client.models.Address
import com.smartcity.client.models.Order

fun AccountViewModel.setSelectedOrder(order: Order){
    val update = getCurrentViewStateOrNew()
    update.viewOrderFields.order=order
    setViewState(update)
}



fun AccountViewModel.setOrdersList(orders:List<Order>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.ordersList=orders
    setViewState(update)
}



fun AccountViewModel.setAddressList(addresses:List<Address>){
    val update = getCurrentViewStateOrNew()
    update.addressList=addresses
    setViewState(update)
}