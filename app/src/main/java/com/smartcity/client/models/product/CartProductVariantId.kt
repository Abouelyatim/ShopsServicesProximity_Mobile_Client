package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartProductVariantId (

    @SerializedName("cartId")
    @Expose
    var cartId: Long,

    @SerializedName("cartProductVariantId")
    @Expose
    var cartProductVariantId: Long

): Parcelable {

    override fun toString(): String {
        return "CartProductVariantId(cartId=$cartId," +
                "cartProductVariantId=$cartProductVariantId)"
    }
}