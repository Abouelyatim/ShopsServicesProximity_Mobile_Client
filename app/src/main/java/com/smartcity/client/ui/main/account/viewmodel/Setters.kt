package com.smartcity.client.ui.main.account.viewmodel

import com.smartcity.client.models.*
import com.smartcity.client.ui.interest.viewmodel.InterestViewModel

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

fun AccountViewModel.setOrderActionList(orderActionList: List<Triple<String,Int,Int>>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderAction = orderActionList
    setViewState(update)
}

fun AccountViewModel.setOrderActionRecyclerPosition(postion:Int){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderActionRecyclerPosition=postion
    setViewState(update)
}

fun AccountViewModel.setAddressList(addresses:List<Address>){
    val update = getCurrentViewStateOrNew()
    update.addressFields.addressList=addresses
    setViewState(update)
}

fun AccountViewModel.setNewAddress(address: Address) {
    val update = getCurrentViewStateOrNew()
    update.addressFields.newAddress=address
    setViewState(update)
}

fun AccountViewModel.clearNewAddress() {
    val update = getCurrentViewStateOrNew()
    update.addressFields.newAddress=null
    update.addressFields.apartmentNumber=null
    update.addressFields.businessName=null
    update.addressFields.doorCodeName=null
    setViewState(update)
}

fun AccountViewModel.setApartmentNumber(value :String){
    val update = getCurrentViewStateOrNew()
    update.addressFields.apartmentNumber = value
    setViewState(update)
}

fun AccountViewModel.setBusinessName(value :String){
    val update = getCurrentViewStateOrNew()
    update.addressFields.businessName = value
    setViewState(update)
}

fun AccountViewModel.setDoorCodeName(value :String){
    val update = getCurrentViewStateOrNew()
    update.addressFields.doorCodeName = value
    setViewState(update)
}

fun AccountViewModel.setDefaultCity(city: City){
    val update = getCurrentViewStateOrNew()
    update.addressFields.defaultCity = city
    setViewState(update)
}

fun AccountViewModel.clearOrderList(){
    val update = getCurrentViewStateOrNew()
    update.orderFields.ordersList= listOf()
    setViewState(update)
}

fun AccountViewModel.setRadius(value: Double) {
    val update = getCurrentViewStateOrNew()
    update.aroundStoresFields.radius= value
    setViewState(update)
}

fun AccountViewModel.setStoresAround(list: List<Store>) {
    val update = getCurrentViewStateOrNew()
    update.aroundStoresFields.stores=list
    setViewState(update)
}

fun AccountViewModel.setUserInformation(infos: UserInformation?) {
    val update = getCurrentViewStateOrNew()
    update.userInformation=infos
    setViewState(update)
}

fun AccountViewModel.setSelectedSortFilter(value: Triple<String,String,String>?) {
    val update = getCurrentViewStateOrNew()
    update.orderFields.selectedSortFilter=value
    setViewState(update)
}

fun AccountViewModel.setSelectedTypeFilter(value: Triple<String,String,String>?) {
    val update = getCurrentViewStateOrNew()
    update.orderFields.selectedTypeFilter=value
    setViewState(update)
}

fun AccountViewModel.setSelectedStatusFilter(value: Triple<String,String,String>?) {
    val update = getCurrentViewStateOrNew()
    update.orderFields.selectedStatusFilter=value
    setViewState(update)
}

fun AccountViewModel.setSearchCity(city: City?){
    val update = getCurrentViewStateOrNew()
    update.aroundStoresFields.searchCity=city
    setViewState(update)
}

fun AccountViewModel.setCityList(list: List<City>) {
    val update = getCurrentViewStateOrNew()
    update.aroundStoresFields.cityList = list
    setViewState(update)
}

fun AccountViewModel.setCityQuery(value: String) {
    val update = getCurrentViewStateOrNew()
    update.aroundStoresFields.cityQuery = value
    setViewState(update)
}

fun AccountViewModel.setSearchRadius(value: Double) {
    val update = getCurrentViewStateOrNew()
    update.aroundStoresFields.searchRadius = value
    setViewState(update)
}

fun AccountViewModel.setSearchStores(list : List<Store>) {
    val update = getCurrentViewStateOrNew()
    update.aroundStoresFields.searchStores = list
    setViewState(update)
}