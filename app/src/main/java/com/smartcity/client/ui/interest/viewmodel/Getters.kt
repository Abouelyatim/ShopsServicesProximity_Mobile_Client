package com.smartcity.client.ui.interest.viewmodel

import com.smartcity.client.models.Address
import com.smartcity.client.models.City
import com.smartcity.client.models.product.Category

fun InterestViewModel.getCategoryList():List<Category>{
    getCurrentViewStateOrNew().let {
        return it.categoryFields.categoryList?: ArrayList()
    }
}

fun InterestViewModel.getSelectedCategoriesList():MutableList<String>{
    getCurrentViewStateOrNew().let {
        return it.categoryFields.selectedCategories?:mutableListOf<String>()
    }
}

fun InterestViewModel.getCountry():String{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.country?:""
    }
}

fun InterestViewModel.getCity():String{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.city?:""
    }
}

fun InterestViewModel.getCityList():List<City>{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.cityList?: listOf()
    }
}

fun InterestViewModel.getSelectedCity():City?{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.selectedCity
    }
}

fun InterestViewModel.getHomeLat():Double{
    getCurrentViewStateOrNew().let {
        if(it.configurationFields.homeLatLong==null){
            return 0.0
        }
        return it.configurationFields.homeLatLong!!.first
    }
}

fun InterestViewModel.getHomeLong():Double{
    getCurrentViewStateOrNew().let {
        if(it.configurationFields.homeLatLong==null){
            return 0.0
        }
        return it.configurationFields.homeLatLong!!.second
    }
}

fun InterestViewModel.getHomeAddress():String{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.homeAddress?:""
    }
}

fun InterestViewModel.getNetworkHomeAddress():Address?{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.networkHomeAddress
    }
}

fun InterestViewModel.getSavedHomeAddress():String{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.savedHomeAddress?:""
    }
}

fun InterestViewModel.getApartmentNumber():String{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.apartmentNumber?:""
    }
}

fun InterestViewModel.getBusinessName():String{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.businessName?:""
    }
}

fun InterestViewModel.getDoorCodeName():String{
    getCurrentViewStateOrNew().let {
        return it.configurationFields.doorCodeName?:""
    }
}

fun InterestViewModel.getFirstName():String{
    getCurrentViewStateOrNew().let {
        return it.userInformationFields.firstName?:""
    }
}

fun InterestViewModel.getLastName():String{
    getCurrentViewStateOrNew().let {
        return it.userInformationFields.lastName?:""
    }
}

fun InterestViewModel.getBirthDay():String{
    getCurrentViewStateOrNew().let {
        return it.userInformationFields.birthDay?:""
    }
}