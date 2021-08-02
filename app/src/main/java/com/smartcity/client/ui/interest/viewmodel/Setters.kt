package com.smartcity.client.ui.interest.viewmodel

import com.smartcity.client.models.Address
import com.smartcity.client.models.City
import com.smartcity.client.models.product.Category

fun InterestViewModel.setCategoryList(categoryList:List<Category>){
    val update = getCurrentViewStateOrNew()
    update.categoryFields.categoryList = categoryList
    setViewState(update)
}

fun InterestViewModel.setUserInterestList(categoryList:List<Category>){
    val update = getCurrentViewStateOrNew()
    update.categoryFields.userInterestList = categoryList
    setViewState(update)
}

fun InterestViewModel.setSelectedCategoriesList(list: MutableList<String>){
    val update = getCurrentViewStateOrNew()
    update.categoryFields.selectedCategories = list
    setViewState(update)
}

fun InterestViewModel.setCountry(value:String){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.country = value
    setViewState(update)
}

fun InterestViewModel.setCity(value:String){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.city = value
    setViewState(update)
}

fun InterestViewModel.setCityList(value:List<City>){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.cityList = value
    setViewState(update)
}

fun InterestViewModel.setSelectedCity(value: City){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.selectedCity = value
    setViewState(update)
}

fun InterestViewModel.setHomeLat(value :Double){
    val update = getCurrentViewStateOrNew()
    var default = 0.0
    if(update.configurationFields.homeLatLong!=null){
        default=update.configurationFields.homeLatLong!!.second
    }
    update.configurationFields.homeLatLong = Pair(value,default)
    setViewState(update)
}

fun InterestViewModel.setHomeLong(value :Double){
    val update = getCurrentViewStateOrNew()
    var default = 0.0
    if(update.configurationFields.homeLatLong!=null){
        default=update.configurationFields.homeLatLong!!.first
    }
    update.configurationFields.homeLatLong = Pair(default,value)
    setViewState(update)
}

fun InterestViewModel.setHomeAddress(value :String){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.homeAddress = value
    setViewState(update)
}

fun InterestViewModel.setNetworkHomeAddress(value :Address){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.networkHomeAddress = value
    setViewState(update)
}

fun InterestViewModel.setSavedHomeAddress(value :String){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.savedHomeAddress = value
    setViewState(update)
}

fun InterestViewModel.setApartmentNumber(value :String){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.apartmentNumber = value
    setViewState(update)
}

fun InterestViewModel.setBusinessName(value :String){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.businessName = value
    setViewState(update)
}

fun InterestViewModel.setDoorCodeName(value :String){
    val update = getCurrentViewStateOrNew()
    update.configurationFields.doorCodeName = value
    setViewState(update)
}