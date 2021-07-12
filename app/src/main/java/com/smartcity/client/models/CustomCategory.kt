package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomCategory(
    @SerializedName("id")
    @Expose
    var pk: Int,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("provider")
    @Expose
    var provider: Int
): Parcelable {
    override fun toString(): String {
        return "CustomCategory(pk=$pk, " +
                "name='$name', " +
                "provider='$provider') "
    }
}