package com.smartcity.client.ui.interest.state

import android.os.Parcelable
import com.smartcity.client.models.product.Category
import kotlinx.android.parcel.Parcelize

const val INTEREST_VIEW_STATE_BUNDLE_KEY = "com.smartcity.client.ui.interest.state.InterestViewState"

@Parcelize
data class InterestViewState(
    var categoryFields: CategoryFields = CategoryFields()


) : Parcelable

@Parcelize
data class CategoryFields(
    var categoryList:List<Category> = ArrayList(),
    var selectedCategories:MutableList<String> = mutableListOf<String>()
) : Parcelable{
    class SelectedCategoriesError {

        companion object{

            fun mustChoose(): String{
                return "You must choose at least three."
            }

            fun none():String{
                return "None"
            }

        }
    }
    fun isValid(): String{
        if(selectedCategories.size<3){
            return SelectedCategoriesError.mustChoose()
        }
        return SelectedCategoriesError.none()
    }

}

