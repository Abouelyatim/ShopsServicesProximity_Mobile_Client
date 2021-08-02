package com.smartcity.client.ui.interest.state

import android.os.Parcelable
import com.smartcity.client.models.Address
import com.smartcity.client.models.City
import com.smartcity.client.models.product.Category
import kotlinx.android.parcel.Parcelize

const val INTEREST_VIEW_STATE_BUNDLE_KEY = "com.smartcity.client.ui.interest.state.InterestViewState"

@Parcelize
data class InterestViewState(
    var categoryFields: CategoryFields = CategoryFields(),
    var configurationFields: ConfigurationFields =ConfigurationFields()

) : Parcelable

@Parcelize
data class ConfigurationFields(
    var country:String ?= null,
    var city:String?= null,
    var cityList: List<City>? = null,
    var selectedCity: City? = null,
    var homeLatLong:Pair<Double,Double> ?= null,
    var savedHomeAddress:String?=null,
    var homeAddress:String?=null,
    var networkHomeAddress:Address?=null,
    var apartmentNumber:String?=null,
    var businessName:String?=null,
    var doorCodeName:String?=null
    ) : Parcelable{
}

@Parcelize
data class CategoryFields(
    var userInterestList:List<Category>? = null,
    var categoryList:List<Category>? = null,
    var selectedCategories:MutableList<String>? = null
) : Parcelable{
    class SelectedCategoriesError {

        companion object{

            fun mustChoose(): String{
                return "You must choose at least one."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValid(): String{
        if(selectedCategories==null){
            return SelectedCategoriesError.mustChoose()
        }
        selectedCategories?.let {
            if(it.size<1){
                return SelectedCategoriesError.mustChoose()
            }
        }
        return SelectedCategoriesError.none()
    }
}

