package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartProductVariant(

    @SerializedName("id")
    @Expose
    var id: CartProductVariantId,

    @SerializedName("productVariant")
    @Expose
    var productVariant: ProductVariants,

    @SerializedName("unit")
    @Expose
    var unit: Int,

    @SerializedName("productImage")
    @Expose
    var productImage: Image,

    @SerializedName("productName")
    @Expose
    var productName: String,

    @SerializedName("storeName")
    @Expose
    var storeName: String

) : Parcelable {
    override fun toString(): String {
        return "CartProductVariant(id=$id," +
                "productVariant=$productVariant," +
                "unit=$unit" +
                "productImage=$productImage" +
                "productName=$productName" +
                "storeName=$storeName)"
    }
}