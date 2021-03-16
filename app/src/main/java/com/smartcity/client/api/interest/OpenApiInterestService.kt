package com.smartcity.client.api.interest

import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.auth.network_responses.LoginResponse
import com.smartcity.client.api.interest.response.ListCategoryResponse
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.util.GenericApiResponse
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


}