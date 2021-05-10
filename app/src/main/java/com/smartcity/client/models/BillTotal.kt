package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BillTotal (
    @SerializedName("policyId")
    @Expose
    var policyId:Long?,

    @SerializedName("total")
    @Expose
    var total:Double?,

    @SerializedName("orderType")
    @Expose
    var orderType:OrderType?
): Parcelable {

    override fun toString(): String {
        return "Bill(policyId=$policyId," +
                "total=$total," +
                "orderType=$orderType)"
    }
}