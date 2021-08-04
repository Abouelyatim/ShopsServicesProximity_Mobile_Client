package com.smartcity.client.api.main

import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.responses.ListAddressResponse
import com.smartcity.client.api.main.responses.ListOrderResponse
import com.smartcity.client.api.main.responses.ListProductResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.*
import com.smartcity.client.models.product.Cart
import com.smartcity.client.models.product.Product
import com.smartcity.client.util.ListGenericResponse
import com.smartcity.provider.models.Policy
import retrofit2.http.*

@MainScope
interface OpenApiMainService {

    @GET("product")
    suspend fun searchListProduct(
        @Query("search") query: String,
        @Query("page") page: Int,
        @Query("id") userId: Long,
    ): ListProductResponse

    @POST("cart/add")
    suspend fun addProductCart(
        @Query("userId") userId: Long,
        @Query("variantId") variantId: Long,
        @Query("quantity") quantity: Int
    ):GenericResponse

    @GET("cart/{userId}")
    suspend fun getUserCart(@Path("userId") id: Long?):Cart

    @DELETE("cart/delete")
    suspend fun deleteProductCart(
        @Query("userId") userId: Long,
        @Query("variantId") variantId: Long
    ):GenericResponse

    @POST("order/create")
    suspend fun placeOrder(
        @Body order: Order
    ):GenericResponse

    @GET("policy/store/{id}")
    suspend fun getStorePolicy(
        @Path("id") storeId: Long
    ):Policy

    @POST("bill/total")
    suspend fun getTotalBill(
        @Body bill: BillTotal
    ):BillTotal

    @POST("address/create")
    suspend fun createAddress(
        @Body address: Address
    ):GenericResponse

    @GET("address/{id}")
    suspend fun getUserAddresses(
        @Path(value = "id") userId: Long
    ):ListAddressResponse

    @DELETE("address/delete/{id}")
    suspend fun deleteUserAddress(
        @Path(value = "id") id: Long
    ):GenericResponse

    @POST("user/Information")
    suspend fun setUserInformation(
        @Body userInformation: UserInformation
    ): GenericResponse

    @GET("user/Information/{id}")
    suspend fun getUserInformation(
        @Path(value = "id") id:Long
    ): UserInformation

    @GET("order/current-user/{id}/inProgress")
    suspend fun getUserInProgressOrders(
        @Path(value = "id") id:Long
    ): ListOrderResponse

    @GET("order/current-user/{id}/finalized")
    suspend fun getUserFinalizedOrders(
        @Path(value = "id") id:Long
    ): ListOrderResponse

    @PUT("order/current-user/{id}/received")
    suspend fun confirmOrderReceived(
        @Path(value = "id") id:Long
    ): GenericResponse

    @GET("user/flash/{id}")
    suspend fun getUserFlashDeals(
        @Path(value = "id") id:Long
    ): ListGenericResponse<FlashDeal>

    @GET("user/offer/{id}")
    suspend fun getUserDiscountProduct(
        @Path(value = "id") id:Long
    ): ListGenericResponse<Product>

    @GET("store/customCategory/store/all/{id}")
    suspend fun getStoreCustomCategory(@Path("id") id: Long?):ListGenericResponse<CustomCategory>

    @GET("product/all/category/{id}")
    suspend fun getProductsByCustomCategory(@Path("id") id: Long?):ListGenericResponse<Product>

    @GET("product/all/store/{id}")
    suspend fun getAllProductsByStore(@Path("id") id: Long?):ListGenericResponse<Product>

    @POST("user/follow/store/{id}/{idUser}")
    suspend fun followStore(
        @Path(value = "id") id: Long,
        @Path(value = "idUser") idUser: Long
    ): GenericResponse

    @POST("user/stop-follow/store/{id}/{idUser}")
    suspend fun stopFollowingStore(
        @Path(value = "id") id: Long,
        @Path(value = "idUser") idUser: Long
    ): GenericResponse

    @GET("user/is-follow/store/{id}/{idUser}")
    suspend fun isFollowingStore(
        @Path(value = "id") id: Long,
        @Path(value = "idUser") idUser: Long
    ): GenericResponse

    @GET("store/store-around")
    suspend fun getStoresAround(
        @Query(value = "distance") distance: Double,
        @Query(value = "longitude") longitude: Double,
        @Query(value = "latitude") latitude: Double
    ): ListGenericResponse<Store>

    @GET("product/interest")
    suspend fun getInterestProduct(
        @Query("page") page: Int,
        @Query("id") userId: Long,
    ): ListProductResponse
}









