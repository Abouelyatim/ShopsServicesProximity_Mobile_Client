package com.smartcity.client.api.main

import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.responses.ListAddressResponse
import com.smartcity.client.api.main.responses.ListOrderResponse
import com.smartcity.client.api.main.responses.ListProductResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.BillTotal
import com.smartcity.client.models.Order
import com.smartcity.client.models.UserInformation
import com.smartcity.client.models.product.Cart
import com.smartcity.client.util.GenericApiResponse
import com.smartcity.provider.models.Policy
import retrofit2.http.*

@MainScope
interface OpenApiMainService {

    @GET("product")
    fun searchListProduct(
        @Query("search") query: String,
        @Query("page") page: Int
    ): LiveData<GenericApiResponse<ListProductResponse>>


    @POST("cart/add")
    fun addProductCart(
        @Query("userId") userId: Long,
        @Query("variantId") variantId: Long,
        @Query("quantity") quantity: Int
    ):LiveData<GenericApiResponse<GenericResponse>>


    @GET("cart/{userId}")
    fun getUserCart(@Path("userId") id: Long?):LiveData<GenericApiResponse<Cart>>


    @DELETE("cart/delete")
    fun deleteProductCart(
        @Query("userId") userId: Long,
        @Query("variantId") variantId: Long
    ):LiveData<GenericApiResponse<GenericResponse>>

    @POST("order/create")
    fun placeOrder(
        @Body order: Order
    ):LiveData<GenericApiResponse<GenericResponse>>

    @GET("policy/store/{id}")
    fun getStorePolicy(
        @Path("id") storeId: Long
    ):LiveData<GenericApiResponse<Policy>>

    @POST("bill/total")
    fun getTotalBill(
        @Body bill: BillTotal
    ):LiveData<GenericApiResponse<BillTotal>>

    @POST("address/create")
    fun createAddress(
        @Body address: Address
    ):LiveData<GenericApiResponse<GenericResponse>>

    @GET("address/{id}")
    fun getUserAddresses(
        @Path(value = "id") userId: Long
    ):LiveData<GenericApiResponse<ListAddressResponse>>

    @DELETE("address/delete/{id}")
    fun deleteUserAddress(
        @Path(value = "id") id: Long
    ):LiveData<GenericApiResponse<GenericResponse>>

    @POST("user/Information")
    fun setUserInformation(
        @Body userInformation: UserInformation
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("user/Information/{id}")
    fun getUserInformation(
        @Path(value = "id") id:Long
    ): LiveData<GenericApiResponse<UserInformation>>

    @GET("order/current-user/{id}/inProgress")
    fun getUserInProgressOrders(
        @Path(value = "id") id:Long
    ): LiveData<GenericApiResponse<ListOrderResponse>>

    @GET("order/current-user/{id}/finalized")
    fun getUserFinalizedOrders(
        @Path(value = "id") id:Long
    ): LiveData<GenericApiResponse<ListOrderResponse>>

    @PUT("order/current-user/{id}/received")
    fun confirmOrderReceived(
        @Path(value = "id") id:Long
    ): LiveData<GenericApiResponse<GenericResponse>>
}









