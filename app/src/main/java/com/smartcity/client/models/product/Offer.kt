package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Offer(
    @SerializedName("discountCode")
    @Expose
    var discountCode:String?,

    @SerializedName("type")
    @Expose
    var type:OfferType?,

    @SerializedName("newPrice")
    @Expose
    var newPrice:Double?,

    @SerializedName("percentage")
    @Expose
    var percentage:Int?,

    @SerializedName("startDate")
    @Expose
    var startDate:String?,

    @SerializedName("endDate")
    @Expose
    var endDate:String?
) : Parcelable {

    override fun toString(): String {
        return "Offer(" +
                "discountCode=$discountCode," +
                "type=$type," +
                "newPrice=$newPrice," +
                "percentage=$percentage," +
                "startDate=$startDate," +
                "endDate=$endDate)"

    }

}