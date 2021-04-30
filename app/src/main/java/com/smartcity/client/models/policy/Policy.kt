package com.smartcity.provider.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Policy(
    @SerializedName("id")
    @Expose
    var id:Long,

    @SerializedName("delivery")
    @Expose
    var delivery:Boolean,

    @SerializedName("selfPickUpOption")
    @Expose
    var selfPickUpOption:SelfPickUpOptions,

    @SerializedName("validDuration")
    @Expose
    var validDuration:Long

) : Parcelable {
    override fun toString(): String {
        return "Policy(id=$id," +
                "delivery=$delivery," +
                "selfPickUpOption=$selfPickUpOption," +
                "validDuration=$validDuration)"
    }
}