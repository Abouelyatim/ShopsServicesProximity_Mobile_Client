package com.smartcity.client.api.auth

import androidx.lifecycle.LiveData
import com.smartcity.client.api.auth.network_responses.CategoryStoreResponse
import com.smartcity.client.util.GenericApiResponse
import com.smartcity.client.api.auth.network_responses.LoginResponse
import com.smartcity.client.api.auth.network_responses.RegistrationResponse
import com.smartcity.client.api.auth.network_responses.StoreResponse
import com.smartcity.client.di.auth.AuthScope
import okhttp3.MultipartBody
import retrofit2.http.*

@AuthScope
interface OpenApiAuthService {

    @POST("user/login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LiveData<GenericApiResponse<LoginResponse>>

    @POST("user/register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("userName") username: String,
        @Field("passWord") password: String,
        @Field("passWord2") password2: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>
}
