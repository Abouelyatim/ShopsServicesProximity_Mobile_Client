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
    var country:String = "",
    var city:String = "",
    var cityList: List<City> = listOf(),
    var selectedCity: City? = null,
    var homeLatLong:Pair<Double,Double> = Pair(0.0,0.0),
    var savedHomeAddress:String="",
    var homeAddress:String="",
    var networkHomeAddress:Address?=null,
    var apartmentNumber:String="",
    var businessName:String="",
    var doorCodeName:String=""
    ) : Parcelable{

}
@Parcelize
data class CategoryFields(
    var categoryList:List<Category> = ArrayList(),
    var selectedCategories:MutableList<String> = mutableListOf<String>()
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
        if(selectedCategories.size<1){
            return SelectedCategoriesError.mustChoose()
        }
        return SelectedCategoriesError.none()
    }
}

