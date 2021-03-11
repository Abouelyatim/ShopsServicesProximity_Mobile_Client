package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductVariantAttributeValue(
    @SerializedName("attributeValue")
    @Expose
    var attributeValue:AttributeValue
) : Parcelable {
    override fun toString(): String {
        return "ProductVariantAttributeValue(attributeValue=$attributeValue)"
    }
}