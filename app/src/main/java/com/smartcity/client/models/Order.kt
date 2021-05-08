package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.client.models.product.CartProductVariantId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    @SerializedName("userId")
    @Expose
    var userId:Long,

    @SerializedName("storeId")
    @Expose
    var storeId:Long,

    @SerializedName("orderType")
    @Expose
    var orderType:OrderType,

    @SerializedName("address")
    @Expose
    var address:Address?,

    @SerializedName("cartProductVariantIds")
    @Expose
    var cartProductVariantIds:List<CartProductVariantId>,

    @SerializedName("receiverFirstName")
    @Expose
    var receiverFirstName:String,

    @SerializedName("receiverLastName")
    @Expose
    var receiverLastName:String,

    @SerializedName("receiverBirthDay")
    @Expose
    var receiverBirthDay:String
) : Parcelable {

    class CreateOrderError {

        companion object{

            fun mustFillUserInformation(): String{
                return "You can't place order without set receiver information."
            }

            fun mustFillDeliveryAddress(): String{
                return "You can't place order without set delivery address."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForCreation(): String{

        if(receiverFirstName.isEmpty()
            || receiverLastName.isEmpty()
            || receiverBirthDay.isEmpty()){
            return CreateOrderError.mustFillUserInformation()
        }

        if(orderType==OrderType.DELIVERY && address==null){
            return CreateOrderError.mustFillDeliveryAddress()
        }

        return CreateOrderError.none()
    }
}