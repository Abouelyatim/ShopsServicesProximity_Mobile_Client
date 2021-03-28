package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart (
    @SerializedName("cartProductVariants")
    @Expose
    var cartProductVariants: List<CartProductVariant>,

    var store:String
): Parcelable {
}