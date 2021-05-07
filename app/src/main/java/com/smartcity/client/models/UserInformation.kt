package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInformation (
    @SerializedName("userId")
    @Expose
    var userId:Long?,
    @SerializedName("firstName")
    @Expose
    var firstName:String,

    @SerializedName("lastName")
    @Expose
    var lastName:String,

    @SerializedName("birthDay")
    @Expose
    var birthDay:String
) : Parcelable {

    override fun toString(): String {
        return "UserInformation(userId=$userId," +
                "firstName=$firstName," +
                "lastName=$lastName," +
                "birthDay=$birthDay)"
    }

    class CreateUserInformationError {

        companion object{

            fun mustFillAllFields(): String{
                return "You can't save without fill all information."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForCreation(): String{
        if(firstName.isEmpty()
            || lastName.isEmpty()
            || birthDay.isEmpty()){
            return CreateUserInformationError.mustFillAllFields()
        }
        return CreateUserInformationError.none()
    }
}