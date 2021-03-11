package com.smartcity.client.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.client.models.product.Product

class ListProductResponse (
    @SerializedName("result")
    @Expose
    var results: List<Product>

) {

    override fun toString(): String {
        return "ListProductResponse(results=$results)"
    }
}