package com.smartcity.client.api.main

import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.responses.*
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.AccountProperties
import com.smartcity.client.models.product.Cart
import com.smartcity.client.models.product.Product
import com.smartcity.client.util.GenericApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@MainScope
interface OpenApiMainService {

    @GET("store/customCategory/all/{id}")
    fun getAllcustomCategory(@Path("id") id: Long?):LiveData<GenericApiResponse<ListCustomCategoryResponse>>

    @GET("product/all/category/{id}")
    fun getAllProductByCategory(@Path("id") id: Long?):LiveData<GenericApiResponse<ListProductResponse>>

    @GET("product/all/provider/{id}")
    fun getAllProduct(@Path("id") id: Long?):LiveData<GenericApiResponse<ListProductResponse>>


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

}









