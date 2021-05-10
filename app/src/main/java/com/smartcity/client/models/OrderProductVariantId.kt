package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderProductVariantId (

    @SerializedName("orderId")
    @Expose
    var orderId: Long,

    @SerializedName("orderProductVariantId")
    @Expose
    var orderProductVariantId: Long

): Parcelable {

    override fun toString(): String {
        return "OrderProductVariantId(orderId=$orderId," +
                "orderProductVariantId=$orderProductVariantId)"
    }
}