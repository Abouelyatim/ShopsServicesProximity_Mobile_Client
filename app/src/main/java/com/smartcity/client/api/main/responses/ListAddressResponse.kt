package com.smartcity.client.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.client.models.Address

class ListAddressResponse (
    @SerializedName("result")
    @Expose
    var results: List<Address>

) {

    override fun toString(): String {
        return "ListAddressResponse(results=$results)"
    }
}