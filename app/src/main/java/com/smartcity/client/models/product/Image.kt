package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    @SerializedName("image")
    @Expose
    var image:String
) : Parcelable {
    override fun toString(): String {
        return "Image(" +
                "image='$image')"
    }
}