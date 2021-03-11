package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product (

    @SerializedName("id")
    @Expose
    var id:Long,

    @SerializedName("description")
    @Expose
    var description: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("images")
    @Expose
    var images: List<Image>,

    @SerializedName("productVariants")
    @Expose
    var productVariants: List<ProductVariants>,

    @SerializedName("attributes")
    @Expose
    var attributes:Set<Attribute>,

    @SerializedName("customCategory")
    @Expose
    var customCategory:Int

) : Parcelable {
    override fun toString(): String {
        return "Product(" +
                "description='$description', " +
                "name='$name', " +
                "images='$images'," +
                "productVariants='$productVariants'" +
                "attribute='$attributes'" +
                "customCategory='$customCategory')"
    }
}