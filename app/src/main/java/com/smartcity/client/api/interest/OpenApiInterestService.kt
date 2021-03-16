package com.smartcity.client.api.interest

import androidx.lifecycle.LiveData
import com.smartcity.client.api.interest.response.ListCategoryResponse
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.util.GenericApiResponse
import retrofit2.http.GET

@InterestScope
interface OpenApiInterestService {

    @GET("category")
    fun getAllCategory(): LiveData<GenericApiResponse<ListCategoryResponse>>
}