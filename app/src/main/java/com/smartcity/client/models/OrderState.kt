package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderState(
    @SerializedName("newOrder")
    @Expose
    var newOrder:Boolean,

    @SerializedName("accepted")
    @Expose
    var accepted:Boolean,

    @SerializedName("rejected")
    @Expose
    var rejected:Boolean,

    @SerializedName("delivered")
    @Expose
    var delivered:Boolean,

    @SerializedName("pickedUp")
    @Expose
    var pickedUp:Boolean,

    @SerializedName("confirmedDelivered")
    @Expose
    var confirmedDelivered:Boolean,

    @SerializedName("confirmedPickedUp")
    @Expose
    var confirmedPickedUp:Boolean

) : Parcelable {
}