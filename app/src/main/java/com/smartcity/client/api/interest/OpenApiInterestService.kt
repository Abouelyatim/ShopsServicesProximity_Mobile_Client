package com.smartcity.client.api.interest

import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.ListGenericDto
import com.smartcity.client.api.interest.dto.CategoryDto
import com.smartcity.client.api.interest.dto.CityDto
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.City
import com.smartcity.client.models.product.Category
import retrofit2.http.*

@InterestScope
interface OpenApiInterestService {

    @GET("category")
    suspend fun getAllCategory(): ListGenericDto<Category, CategoryDto>

    @POST("user/interestCenter")
    @FormUrlEncoded
    suspend fun setInterestCenter(
        @Field("id") id: Long,
        @Field("interest") interest: List<String>
    ): GenericResponse

    @GET("user/interestCenter/{id}")
    suspend fun getUserInterestCenter(@Path("id") id: Long?): ListGenericDto<Category, CategoryDto>

    @GET("nominatim/search")
    suspend fun resolveUserCity(
        @Query(value = "country") country: String,
        @Query(value = "city") city: String
    ): ListGenericDto<City, CityDto>

    @POST("address/create")
    suspend fun createAddress(
        @Body address: Address
    ):GenericResponse

    @POST("user/default-city")
    suspend fun setUserDefaultCity(
        @Body city: City
    ):GenericResponse
}