package com.smartcity.client.api.interest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.client.api.GenericDto
import com.smartcity.client.models.product.Category

class CategoryDto(

    @SerializedName("id")
    @Expose
    var id:Long,

    @SerializedName("name")
    @Expose
    var name:String,

    @SerializedName("subCategorys")
    @Expose
    var subCategorys:List<String>

): GenericDto<Category> {

    override fun toModel():Category{
        return Category(
            id= id,
            name = name,
            subCategorys = subCategorys
        )
    }

    override fun toString(): String {
        return "Category(" +
                "id='$id'" +
                "name='$name'" +
                "subCategorys='$subCategorys')"
    }
}