package com.smartcity.client.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomCategoryResponse(
    @SerializedName("id")
    @Expose
    var pk: Int,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("provider")
    @Expose
    var provider: Int
) {
    override fun toString(): String {
        return "CustomCategoryResponse(pk=$pk," +
                "name='$name', " +
                "provider='$provider') "
    }
}