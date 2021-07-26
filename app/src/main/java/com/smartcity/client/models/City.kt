package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City (
    @SerializedName("latitude")
    @Expose
    var lat: Double,

    @SerializedName("longitude")
    @Expose
    var lon: Double,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("userId")
    @Expose
    var userId: Long
) : Parcelable {
    override fun toString(): String {
        return "City(lat=$lat, " +
                "lon='$lon', " +
                "city='$name') "
    }
}