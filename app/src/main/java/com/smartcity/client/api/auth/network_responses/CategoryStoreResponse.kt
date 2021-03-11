package com.smartcity.client.api.auth.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CategoryStoreResponse(
    @SerializedName("result")
    @Expose
    var response: List<String>
)