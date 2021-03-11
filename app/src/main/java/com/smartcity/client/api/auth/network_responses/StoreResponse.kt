package com.smartcity.client.api.auth.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StoreResponse (

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("description")
    @Expose
    var description: String,

    @SerializedName("address")
    @Expose
    var address: String,

    @SerializedName("provider")
    @Expose
    var provider: Long
    )
{
    override fun toString(): String {
        return "StoreResponse(name='$name', description='$description', address='$address', provider=$provider')"
    }
}