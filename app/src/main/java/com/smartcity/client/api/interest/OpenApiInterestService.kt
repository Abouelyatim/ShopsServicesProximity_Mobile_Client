package com.smartcity.client.api.interest

import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.interest.response.ListCategoryResponse
import com.smartcity.client.api.main.responses.ListGenericResponse
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.City
import com.smartcity.client.util.deleted.GenericApiResponse
import retrofit2.http.*

@InterestScope
interface OpenApiInterestService {

    @GET("category")
    fun getAllCategory(): LiveData<GenericApiResponse<ListCategoryResponse>>

    @POST("user/interestCenter")
    @FormUrlEncoded
    fun setInterestCenter(
        @Field("id") id: Long,
        @Field("interest") interest: List<String>
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("user/interestCenter/{id}")
    fun getUserInterestCenter(@Path("id") id: Long?): LiveData<GenericApiResponse<ListCategoryResponse>>

    @GET("nominatim/search")
    fun resolveUserCity(
        @Query(value = "country") country: String,
        @Query(value = "city") city: String
    ): LiveData<GenericApiResponse<ListGenericResponse<City>>>

    @POST("address/create")
    fun createAddress(
        @Body address: Address
    ):LiveData<GenericApiResponse<GenericResponse>>

    @POST("user/default-city")
    fun setUserDefaultCity(
        @Body city: City
    ):LiveData<GenericApiResponse<GenericResponse>>
}