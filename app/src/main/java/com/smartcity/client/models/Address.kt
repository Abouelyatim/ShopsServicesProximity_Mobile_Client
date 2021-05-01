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

    @SerializedName("houseNumber")
    @Expose
    var houseNumber:Int?,

    @SerializedName("street")
    @Expose
    var street:String,

    @SerializedName("city")
    @Expose
    var city:String,

    @SerializedName("zipCode")
    @Expose
    var zipCode:Long?,

    @SerializedName("userId")
    @Expose
    var userId:Long
) : Parcelable {

    override fun toString(): String {
        return "Address(id=$id," +
                "houseNumber=$houseNumber," +
                "street=$street" +
                "city=$city," +
                "zipCode=$zipCode," +
                "userId=$userId)"
    }

    class CreateAddressError {

        companion object{

            fun mustFillAllFields(): String{
                return "You can't create address without fill all information."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForCreation(): String{
        if(street.isEmpty()
            || city.isEmpty()
            || houseNumber==null
            || zipCode==null){
            return CreateAddressError.mustFillAllFields()
        }
        return CreateAddressError.none()
    }
}