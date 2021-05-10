package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bill(
    @SerializedName("total")
    @Expose
    var total:Double,

    @SerializedName("alreadyPaid")
    @Expose
    var alreadyPaid:Double,

    @SerializedName("createdAt")
    @Expose
    var createdAt:String
) : Parcelable {
}