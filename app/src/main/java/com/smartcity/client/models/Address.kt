package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    @SerializedName("id")
    @Expose
    var id:Long?,

    @SerializedName("streetNumber")
    @Expose
    var streetNumber:String?,

    @SerializedName("admin")
    @Expose
    var admin:String?,

    @SerializedName("subAdmin")
    @Expose
    var subAdmin:String?,

    @SerializedName("locality")
    @Expose
    var locality:String?,

    @SerializedName("streetName")
    @Expose
    var streetName:String?,

    @SerializedName("postalCode")
    @Expose
    var postalCode:String?,

    @SerializedName("countryCode")
    @Expose
    var countryCode:String,

    @SerializedName("countryName")
    @Expose
    var countryName:String,

    @SerializedName("latitude")
    @Expose
    var latitude:Double,

    @SerializedName("longitude")
    @Expose
    var longitude:Double,

    @SerializedName("fullAddress")
    @Expose
    var fullAddress:String,

    @SerializedName("apartmentNumber")
    @Expose
    var apartmentNumber:String,

    @SerializedName("businessName")
    @Expose
    var businessName:String,

    @SerializedName("doorCodeName")
    @Expose
    var doorCodeName:String,

    @SerializedName("userId")
    @Expose
    var userId:Long
) : Parcelable {

    override fun toString(): String {
        return "Address(id=$id," +
                "streetNumber=$streetNumber," +
                "admin=$admin," +
                "subAdmin=$subAdmin," +
                "locality=$locality," +
                "streetName=$streetName," +
                "postalCode=$postalCode," +
                "countryCode=$countryCode," +
                "countryName=$countryName," +
                "latitude=$latitude," +
                "longitude=$longitude," +
                "fullAddress=$fullAddress," +
                "apartmentNumber=$apartmentNumber," +
                "businessName=$businessName," +
                "doorCodeName=$doorCodeName," +
                "userId=$userId)"
    }
}