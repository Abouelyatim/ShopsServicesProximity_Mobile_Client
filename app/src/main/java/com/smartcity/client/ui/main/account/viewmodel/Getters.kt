package com.smartcity.client.ui.main.account.viewmodel

import com.smartcity.client.models.*
import com.smartcity.client.ui.interest.viewmodel.InterestViewModel

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
        return it.addressFields.addressList?: listOf()
    }
}

fun AccountViewModel.getNewAddress(): Address? {
    getCurrentViewStateOrNew().let {
        return it.addressFields.newAddress
    }
}

fun AccountViewModel.getApartmentNumber():String{
    getCurrentViewStateOrNew().let {
        return it.addressFields.apartmentNumber?:""
    }
}

fun AccountViewModel.getBusinessName():String{
    getCurrentViewStateOrNew().let {
        return it.addressFields.businessName?:""
    }
}

fun AccountViewModel.getDoorCodeName():String{
    getCurrentViewStateOrNew().let {
        return it.addressFields.doorCodeName?:""
    }
}

fun AccountViewModel.getDefaultCity():City?{
    getCurrentViewStateOrNew().let {
        return it.addressFields.defaultCity
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