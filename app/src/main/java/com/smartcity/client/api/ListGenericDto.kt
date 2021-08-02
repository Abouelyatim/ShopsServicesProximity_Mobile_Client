package com.smartcity.client.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListGenericDto<P,T : GenericDto<P>> (
    @SerializedName("result")
    @Expose
    var results: List<T>
) {

    fun toList():List<P>{
        val pList: ArrayList<P> = ArrayList()
        for(tDto in results){
            pList.add(
                tDto.toModel()
            )
        }
        return pList
    }

    override fun toString(): String {
        return "ListCityResponse(results=$results)"
    }
}