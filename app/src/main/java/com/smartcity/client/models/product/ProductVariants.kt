package com.smartcity.client.models.product

import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductVariants(

    @SerializedName("id")
    @Expose
    var id: Long,

    @SerializedName("productVariantAttributeValuesProductVariant")
    @Expose
    var productVariantAttributeValuesProductVariant: List<ProductVariantAttributeValue>,

    @SerializedName("image")
    @Expose
    var image: String?,

    @SerializedName("price")
    @Expose
    var price: Double,

    @SerializedName("unit")
    @Expose
    var unit: Int,

    @Transient
    var  imageUri: Uri?
) : Parcelable {
    override fun toString(): String {
        return "ProductVariants(" +
                "productVariantAttributeValuesProductVariant='$productVariantAttributeValuesProductVariant', " +
                "image='$image', " +
                "price='$price', " +
                "unit='$unit')"
    }
}