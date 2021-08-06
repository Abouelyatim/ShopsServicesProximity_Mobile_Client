package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlashDeal(
    @SerializedName("id")
    @Expose
    var id:Long?,

    @SerializedName("title")
    @Expose
    var title:String?,

    @SerializedName("content")
    @Expose
    var content:String?,

    @SerializedName("createAt")
    @Expose
    var createAt:String?,

    @SerializedName("storeName")
    @Expose
    var storeName:String?,

    @SerializedName("storeAddress")
    @Expose
    var storeAddress:String?,

    @SerializedName("latitude")
    @Expose
    var latitude:Double?,

    @SerializedName("longitude")
    @Expose
    var longitude:Double?

) : Parcelable {
    override fun toString(): String {
        return "FlashDeal(id=$id," +
                "title=$title," +
                "content=$content," +
                "storeName=$storeName," +
                "storeAddress=$storeAddress" +
                "latitude=$latitude" +
                "longitude=$longitude" +
                ")"
    }

    class CreateFlashError {

        companion object{

            fun mustFillAllFields(): String{
                return "You can't create flash without fill all information."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForCreation(): String{
        if(title.isNullOrBlank()
            || title.isNullOrEmpty()
            || content.isNullOrBlank()
            || content.isNullOrEmpty()){
            return CreateFlashError.mustFillAllFields()
        }
        return CreateFlashError.none()
    }
}