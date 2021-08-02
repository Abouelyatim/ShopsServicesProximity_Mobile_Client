package com.smartcity.client.api.interest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.client.api.GenericDto
import com.smartcity.client.models.City

class CityDto(

    @SerializedName("lat")
    @Expose
    var lat: Double,

    @SerializedName("lon")
    @Expose
    var lon: Double,

    @SerializedName("city")
    @Expose
    var name: String
) : GenericDto<City> {

    override fun toModel(): City {
        return City(
            lat = lat,
            lon = lon,
            name = name,
            userId = -1
        )
    }

    override fun toString(): String {
        return "City(lat=$lat, " +
                "lon='$lon', " +
                "city='$name') "
    }
}