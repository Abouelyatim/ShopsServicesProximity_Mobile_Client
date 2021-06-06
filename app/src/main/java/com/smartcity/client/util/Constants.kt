package com.smartcity.client.util

class Constants {

    companion object{

        const val BASE_URL = "http://192.168.42.49:8085/api/"
        const val PRODUCT_IMAGE_URL=BASE_URL+"product/image/"

        const val PASSWORD_RESET_URL: String = "https://open-api.xyz/password_reset/"

        const val LOCAL_STORAGE_DIRECTORY="ImagePicker"

        const val NETWORK_TIMEOUT = 6000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        const val PAGINATION_PAGE_SIZE = 10

        const val GALLERY_REQUEST_CODE = 201
        const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
        const val CROP_IMAGE_INTENT_CODE: Int = 401

        const val DINAR_ALGERIAN=" DA"
    }
}