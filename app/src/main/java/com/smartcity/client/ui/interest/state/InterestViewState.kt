package com.smartcity.client.ui.interest.state

import android.os.Parcelable
import com.smartcity.client.models.AuthToken
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
    var selectedCategories:MutableMap<String, MutableList<String>> = mutableMapOf()
) : Parcelable

