package com.smartcity.client.ui.main.cart.viewmodel

import com.smartcity.client.models.*
import com.smartcity.client.models.product.Cart
import com.smartcity.client.ui.main.account.viewmodel.AccountViewModel
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
    if(addresses.isNotEmpty() && update.orderFields.deliveryAddress==null){
        update.orderFields.deliveryAddress=addresses.first()
    }
    setViewState(update)
}

fun CartViewModel.setDeliveryAddress(address: Address){
    val update = getCurrentViewStateOrNew()
    update.orderFields.deliveryAddress=address
    setViewState(update)
}

fun CartViewModel.clearNewAddress() {
    val update = getCurrentViewStateOrNew()
    update.orderFields.deliveryAddress=null
    update.orderFields.apartmentNumber=null
    update.orderFields.businessName=null
    update.orderFields.doorCodeName=null
    setViewState(update)
}

fun CartViewModel.setApartmentNumber(value :String){
    val update = getCurrentViewStateOrNew()
    update.orderFields.apartmentNumber = value
    setViewState(update)
}

fun CartViewModel.setBusinessName(value :String){
    val update = getCurrentViewStateOrNew()
    update.orderFields.businessName = value
    setViewState(update)
}

fun CartViewModel.setDoorCodeName(value :String){
    val update = getCurrentViewStateOrNew()
    update.orderFields.doorCodeName = value
    setViewState(update)
}

fun CartViewModel.setDefaultCity(city: City){
    val update = getCurrentViewStateOrNew()
    update.orderFields.defaultCity = city
    setViewState(update)
}

fun CartViewModel.setUserInformation(userInformation: UserInformation){
    val update = getCurrentViewStateOrNew()
    update.orderFields.userInformation=userInformation
    setViewState(update)
}