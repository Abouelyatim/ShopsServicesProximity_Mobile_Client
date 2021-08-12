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

fun AccountViewModel.getSelectedSortFilter(): Triple<String,String,String>? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.selectedSortFilter
    }
}

fun AccountViewModel.getSelectedTypeFilter(): Triple<String,String,String>? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.selectedTypeFilter
    }
}

fun AccountViewModel.getSelectedStatusFilter(): Triple<String,String,String>? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.selectedStatusFilter
    }
}

fun AccountViewModel.getSearchCity(): City? {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.searchCity
    }
}

fun AccountViewModel.getCityList(): List<City> {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.cityList?: listOf()
    }
}

fun AccountViewModel.getCityQuery(): String {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.cityQuery?:""
    }
}

fun AccountViewModel.getSearchRadius(): Double {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.searchRadius?:12.0
    }
}

fun AccountViewModel.checkSearchRadius(): Boolean {
    getCurrentViewStateOrNew().let {
        it.aroundStoresFields.searchRadius?.let {
            return true
        }
        return false
    }
}

fun AccountViewModel.getSearchStores(): List<Store> {
    getCurrentViewStateOrNew().let {
        return it.aroundStoresFields.searchStores?: listOf()
    }
}