package com.smartcity.client.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomCategory(
    var pk: Int,
    var name: String,
    var provider: Int
): Parcelable {
    override fun toString(): String {
        return "CustomCategory(pk=$pk, " +
                "name='$name', " +
                "provider='$provider') "
    }
}