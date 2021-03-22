package com.smartcity.client.api.main

import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.responses.*
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.AccountProperties
import com.smartcity.client.models.product.Product
import com.smartcity.client.util.GenericApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@MainScope
interface OpenApiMainService {

    @GET("store/customCategory/all/{id}")
    fun getAllcustomCategory(@Path("id") id: Long?):LiveData<GenericApiResponse<ListCustomCategoryResponse>>

    @POST("store/customCategory/create")
    @FormUrlEncoded
    fun createCustomCategory(
        @Field("provider") provider:Long,
        @Field("name") name:String
    ): LiveData<GenericApiResponse<CustomCategoryResponse>>

    @DELETE("store/customCategory/delete/{id}")
    fun deleteCustomCategory(@Path("id") id: Long?):LiveData<GenericApiResponse<GenericResponse>>

    @PUT("store/customCategory/update")
    @FormUrlEncoded
    fun updateCustomCategory(
        @Field("id") id:Long,
        @Field("name") name:String,
        @Field("provider") provider:Long
    ): LiveData<GenericApiResponse<CustomCategoryResponse>>


    @Multipart
    @POST("product/create")
    fun createProduct(
        @Part("product")  product: RequestBody,
        @Part productImagesFile: List<MultipartBody.Part>,
        @Part variantesImagesFile : List<MultipartBody.Part>
    ): LiveData<GenericApiResponse<Product>>

    @Multipart
    @PUT("product/update")
    fun updateProduct(
        @Part("product")  product: RequestBody,
        @Part productImagesFile: List<MultipartBody.Part>,
        @Part variantesImagesFile : List<MultipartBody.Part>
    ): LiveData<GenericApiResponse<Product>>

    @GET("product/all/category/{id}")
    fun getAllProductByCategory(@Path("id") id: Long?):LiveData<GenericApiResponse<ListProductResponse>>

    @GET("product/all/provider/{id}")
    fun getAllProduct(@Path("id") id: Long?):LiveData<GenericApiResponse<ListProductResponse>>


    @DELETE("product/delete/{id}")
    fun deleteProduct(@Path("id") id: Long?):LiveData<GenericApiResponse<GenericResponse>>



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






}









