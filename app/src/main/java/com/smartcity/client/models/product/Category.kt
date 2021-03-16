package com.smartcity.client.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    @SerializedName("id")
    @Expose
    var id:Long,
    @SerializedName("name")
    @Expose
    var name:String,
    @SerializedName("subCategorys")
    @Expose
    var subCategorys:List<String>
) : Parcelable {

    override fun toString(): String {
        return "Category(" +
                "id='$id'" +
                "name='$name'" +
                "subCategorys='$subCategorys')"
    }
}