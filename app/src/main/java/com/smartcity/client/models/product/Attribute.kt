package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attribute(

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("attributeValues")
    @Expose
    var attributeValues:MutableSet<AttributeValue>

) : Parcelable {
    override fun toString(): String {
        return "Attribute(name=$name," +
                "attributeValues=$attributeValues)"
    }
}