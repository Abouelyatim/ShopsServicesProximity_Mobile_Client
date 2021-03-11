package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AttributeValue(
    @SerializedName("value")
    @Expose
    var value: String,

    @SerializedName("attribute")
    @Expose
    var attribute: String

) : Parcelable {
    override fun toString(): String {
        return "AttributeValue(value='$value'" +
                "attribute='$attribute' )"
    }
}