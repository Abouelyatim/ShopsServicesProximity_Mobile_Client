package com.smartcity.client.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListCustomCategoryResponse (
    @SerializedName("result")
    @Expose
    var results: List<CustomCategoryResponse>

) {

    override fun toString(): String {
        return "ListCustomCategoryResponse(results=$results)"
    }
}