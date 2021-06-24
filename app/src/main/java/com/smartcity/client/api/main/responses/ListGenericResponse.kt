package com.smartcity.client.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListGenericResponse<T> (
    @SerializedName("result")
    @Expose
    var results: List<T>

) {

    override fun toString(): String {
        return "ListGenericResponse(results=$results)"
    }
}