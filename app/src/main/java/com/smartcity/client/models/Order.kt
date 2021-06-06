package com.smartcity.client.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.client.models.product.CartProductVariantId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    @SerializedName("id")
    @Expose
    var id:Long?,

    @SerializedName("userId")
    @Expose
    var userId:Long?,

    @SerializedName("storeId")
    @Expose
    var storeId:Long?,

    @SerializedName("bill")
    @Expose
    var bill:Bill?,

    @SerializedName("orderType")
    @Expose
    var orderType:OrderType?,

    @SerializedName("address")
    @Expose
    var address:Address?,

    @SerializedName("cartProductVariantIds")
    @Expose
    var cartProductVariantIds:List<CartProductVariantId>?,

    @SerializedName("orderProductVariants")
    @Expose
    var orderProductVariants: List<OrderProductVariant>?,

    @SerializedName("orderState")
    @Expose
    var orderState:OrderState?,

    @SerializedName("receiverFirstName")
    @Expose
    var firstName:String?,

    @SerializedName("receiverLastName")
    @Expose
    var lastName:String?,

    @SerializedName("receiverBirthDay")
    @Expose
    var birthDay:String?,

    @SerializedName("createAt")
    @Expose
    var createAt:String?,

    @SerializedName("validDuration")
    @Expose
    var validDuration:Long?,

    @SerializedName("storeName")
    @Expose
    var storeName:String?,

    @SerializedName("storeAddress")
    @Expose
    var storeAddress:String?
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

       /* if(firstName.isEmpty()
            || lastName.isEmpty()
            || birthDay.isEmpty()){
            return CreateOrderError.mustFillUserInformation()
        }*/

        if(orderType==OrderType.DELIVERY && address==null){
            return CreateOrderError.mustFillDeliveryAddress()
        }

        return CreateOrderError.none()
    }
}