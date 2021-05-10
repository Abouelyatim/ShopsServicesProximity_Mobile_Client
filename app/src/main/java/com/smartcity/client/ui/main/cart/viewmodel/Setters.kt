package com.smartcity.client.ui.main.cart.viewmodel

import com.smartcity.client.models.Address
import com.smartcity.client.models.BillTotal
import com.smartcity.client.models.OrderType
import com.smartcity.client.models.UserInformation
import com.smartcity.client.models.product.Cart
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.provider.models.Policy

fun CartViewModel.setCartList(cart: Cart){
    val update = getCurrentViewStateOrNew()
    update.cartFields.cartList=cart
    setViewState(update)
}

fun CartViewModel.clearCartList(){
    val update = getCurrentViewStateOrNew()
    update.cartFields.cartList= Cart(listOf(),-1,"")
    setViewState(update)
}

fun CartViewModel.setStorePolicy(policy: Policy){
    val update = getCurrentViewStateOrNew()
    update.orderFields.storePolicy=policy
    setViewState(update)
}

fun CartViewModel.setSelectedCartProduct(cart: Cart){
    val update = getCurrentViewStateOrNew()
    update.orderFields.selectedCartProduct=cart
    setViewState(update)
}

fun CartViewModel.setTotalBill(bill: BillTotal){
    val update = getCurrentViewStateOrNew()
    update.orderFields.total=bill
    setViewState(update)
}

fun CartViewModel.setOrderType(orderType: OrderType){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderType=orderType
    setViewState(update)
}

fun CartViewModel.clearOrderFields(){
    val update = getCurrentViewStateOrNew()
    update.orderFields= CartViewState.OrderFields()
    setViewState(update)
}

fun CartViewModel.setAddressList(addresses:List<Address>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.addressList=addresses
    if(addresses.isNotEmpty()){
        update.orderFields.deliveryAddress=addresses.first()
    }
    setViewState(update)
}

fun CartViewModel.setDeliveryAddress(address: Address){
    val update = getCurrentViewStateOrNew()
    update.orderFields.deliveryAddress=address
    setViewState(update)
}

fun CartViewModel.setUserInformation(userInformation: UserInformation){
    val update = getCurrentViewStateOrNew()
    update.orderFields.userInformation=userInformation
    setViewState(update)
}