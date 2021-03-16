package com.smartcity.client.api.interest.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.client.models.product.Category

class ListCategoryResponse(
    @SerializedName("result")
    @Expose
    var results: List<Category>
) {

    override fun toString(): String {
        return "ListCategoryResponse(results=$results)"
    }
}