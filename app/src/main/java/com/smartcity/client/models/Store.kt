package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.client.models.product.Category
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
    @SerializedName("id")
    @Expose
    var id:Long,

    @SerializedName("name")
    @Expose
    var name:String,

    @SerializedName("description")
    @Expose
    var description:String,

    @SerializedName("storeAddress")
    @Expose
    var storeAddress:StoreAddress,

    @SerializedName("imageStore")
    @Expose
    var imageStore:String,

    @SerializedName("defaultCategories")
    @Expose
    var defaultCategories: List<Category>
) : Parcelable {

    override fun toString(): String {
        return "Store(" +
                "id='$id'" +
                "name='$name'" +
                "description='$description'" +
                "storeAddress='$storeAddress'" +
                "imageStore='$imageStore'" +
                "defaultCategories='$defaultCategories'" +
                ")"
    }
}