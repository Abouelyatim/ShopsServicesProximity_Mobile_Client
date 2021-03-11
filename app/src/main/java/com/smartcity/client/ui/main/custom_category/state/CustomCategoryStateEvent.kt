package com.smartcity.client.ui.main.custom_category.state

import com.smartcity.client.models.product.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody

sealed class CustomCategoryStateEvent {

    class CustomCategoryMain : CustomCategoryStateEvent()

    data class CreateCustomCategory(
        val name:String
    ):CustomCategoryStateEvent()

    data class DeleteCustomCategory(
        val id:Long
    ):CustomCategoryStateEvent()

    data class UpdateCustomCategory(
        val id:Long,
        val name: String,
        val provider:Long
    ):CustomCategoryStateEvent()

    data class CreateProduct(
        val product: RequestBody,
        val productImagesFile: List<MultipartBody.Part>,
        val variantesImagesFile : List<MultipartBody.Part>,
        val productObject: Product
    ):CustomCategoryStateEvent()

    data class UpdateProduct(
        val product: RequestBody,
        val productImagesFile: List<MultipartBody.Part>,
        val variantesImagesFile : List<MultipartBody.Part>,
        val productObject: Product
    ):CustomCategoryStateEvent()

    class ProductMain(
        val id: Long
    ) : CustomCategoryStateEvent()

    data class DeleteProduct(
        val id:Long
    ):CustomCategoryStateEvent()

    class None: CustomCategoryStateEvent()
}