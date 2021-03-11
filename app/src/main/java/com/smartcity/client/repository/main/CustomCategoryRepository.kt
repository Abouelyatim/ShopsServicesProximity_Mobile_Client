package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.CustomCategoryResponse
import com.smartcity.client.api.main.responses.ListCustomCategoryResponse
import com.smartcity.client.api.main.responses.ListProductResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.repository.JobManager
import com.smartcity.client.repository.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Response
import com.smartcity.client.ui.ResponseType
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState.*
import com.smartcity.client.util.*
import com.smartcity.client.util.SuccessHandling.Companion.DELETE_DONE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@MainScope
class CustomCategoryRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("CustomCategoryRepository") {

    private val TAG: String = "AppDebug"

    fun attemptCustomCategoryMain(
        id: Long
    ): LiveData<DataState<CustomCategoryViewState>> {
        return object :
            NetworkBoundResource<ListCustomCategoryResponse, List<CustomCategory>, CustomCategoryViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListCustomCategoryResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")
                val customCategoryList: ArrayList<CustomCategory> = ArrayList()

                for (customCategoryResponse in response.body.results) {
                    customCategoryList.add(
                        CustomCategory(
                            pk = customCategoryResponse.pk,
                            name = customCategoryResponse.name,
                            provider = customCategoryResponse.provider
                        )
                    )
                }

                onCompleteJob(
                    DataState.data(
                        data = CustomCategoryViewState(
                            customCategoryFields = CustomCategoryFields(customCategoryList)
                        ),
                        response = null
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListCustomCategoryResponse>> {
                return openApiMainService.getAllcustomCategory(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<CustomCategoryViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: List<CustomCategory>?) {

            }

            override fun setJob(job: Job) {
                addJob("CustomCategoryMainAttemp", job)
            }

        }.asLiveData()
    }

    fun attemptCreateCustomCategory(
        id: Long,
        name :String): LiveData<DataState<CustomCategoryViewState>> {
        return object :
            NetworkBoundResource<CustomCategoryResponse, CustomCategory, CustomCategoryViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<CustomCategoryResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_CREATION_DONE,
                            ResponseType.SnackBar()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<CustomCategoryResponse>> {

                return openApiMainService.createCustomCategory(
                    provider = id,
                    name = name
                )
            }

            override fun loadFromCache(): LiveData<CustomCategoryViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptCreateCustomCategory", job)
            }

            override suspend fun updateLocalDb(cacheObject: CustomCategory?) {

            }

        }.asLiveData()
    }


    fun attemptdeleteCustomCategory(
        id: Long): LiveData<DataState<CustomCategoryViewState>> {
        return object :
            NetworkBoundResource<GenericResponse, CustomCategory, CustomCategoryViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if(response.body.response ==DELETE_DONE){
                    onCompleteJob(
                        DataState.data(
                            data = null
                            ,
                            response = Response(
                                SuccessHandling.DELETE_DONE,
                                ResponseType.SnackBar()
                            )
                        )
                    )
                }else{
                    onCompleteJob(
                        DataState.error(
                            Response(
                                ErrorHandling.ERROR_UNKNOWN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {

                return openApiMainService.deleteCustomCategory(
                    id=id
                )
            }

            override fun loadFromCache(): LiveData<CustomCategoryViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptdeleteCustomCategory", job)
            }

            override suspend fun updateLocalDb(cacheObject: CustomCategory?) {

            }

        }.asLiveData()
    }



    fun attemptUpdateCustomCategory(
        id: Long,
        name :String,
        provider:Long): LiveData<DataState<CustomCategoryViewState>> {
        return object :
            NetworkBoundResource<CustomCategoryResponse, CustomCategory, CustomCategoryViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<CustomCategoryResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            ResponseType.SnackBar()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<CustomCategoryResponse>> {

                return openApiMainService.updateCustomCategory(
                    id=id,
                    name = name,
                    provider = provider
                )
            }

            override fun loadFromCache(): LiveData<CustomCategoryViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptUpdateCustomCategory", job)
            }

            override suspend fun updateLocalDb(cacheObject: CustomCategory?) {

            }

        }.asLiveData()
    }



    fun attemptCreateProduct(
        product: RequestBody,
        productImagesFile: List<MultipartBody.Part>,
        variantesImagesFile : List<MultipartBody.Part>,
        productObject: Product
    ): LiveData<DataState<CustomCategoryViewState>> {


        val productFieldsErrors = ProductFields(
            productObject.description,
            productObject.name,
            "",
            "",
            mutableListOf(),
            productObject.productVariants.toMutableList()).isValidForCreation()

        if(!productFieldsErrors.equals(ProductFields.CreateProductError.none())){
            return returnErrorResponse(productFieldsErrors, ResponseType.Dialog())

        }
        return object :
            NetworkBoundResource<Product, Product, CustomCategoryViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Product>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.PRODUCT_CREATION_DONE,
                            ResponseType.SnackBar()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<Product>> {

                return openApiMainService.createProduct(
                    product=product,
                    productImagesFile=productImagesFile,
                    variantesImagesFile=variantesImagesFile
                )
            }

            override fun loadFromCache(): LiveData<CustomCategoryViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptUpdateCustomCategory", job)
            }

            override suspend fun updateLocalDb(cacheObject: Product?) {

            }

        }.asLiveData()
    }


    fun attemptUpdateProduct(
        product: RequestBody,
        productImagesFile: List<MultipartBody.Part>,
        variantesImagesFile : List<MultipartBody.Part>,
        productObject: Product
    ): LiveData<DataState<CustomCategoryViewState>> {


        val productFieldsErrors = ProductFields(
            productObject.description,
            productObject.name,
            "",
            "",
            mutableListOf(),
            productObject.productVariants.toMutableList()).isValidForCreation()

        if(!productFieldsErrors.equals(ProductFields.CreateProductError.none())){
            return returnErrorResponse(productFieldsErrors, ResponseType.Dialog())

        }
        return object :
            NetworkBoundResource<Product, Product, CustomCategoryViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Product>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.PRODUCT_UPDATE_DONE,
                            ResponseType.SnackBar()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<Product>> {

                return openApiMainService.updateProduct(
                    product=product,
                    productImagesFile=productImagesFile,
                    variantesImagesFile=variantesImagesFile
                )
            }

            override fun loadFromCache(): LiveData<CustomCategoryViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptUpdateProduct", job)
            }

            override suspend fun updateLocalDb(cacheObject: Product?) {

            }

        }.asLiveData()
    }

    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<CustomCategoryViewState>>{
        Log.d(TAG, "returnErrorResponse: ${errorMessage}")

        return object: LiveData<DataState<CustomCategoryViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )
            }
        }
    }

    fun attemptDeleteProduct(
        id: Long): LiveData<DataState<CustomCategoryViewState>> {
        return object :
            NetworkBoundResource<GenericResponse, CustomCategory, CustomCategoryViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if(response.body.response ==DELETE_DONE){
                    onCompleteJob(
                        DataState.data(
                            data = null
                            ,
                            response = Response(
                                SuccessHandling.DELETE_DONE,
                                ResponseType.SnackBar()
                            )
                        )
                    )
                }else{
                    onCompleteJob(
                        DataState.error(
                            Response(
                                ErrorHandling.ERROR_UNKNOWN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {

                return openApiMainService.deleteProduct(
                    id=id
                )
            }

            override fun loadFromCache(): LiveData<CustomCategoryViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptDeleteProduct", job)
            }

            override suspend fun updateLocalDb(cacheObject: CustomCategory?) {

            }

        }.asLiveData()
    }



    fun attemptProductMain(
        id: Long
    ): LiveData<DataState<CustomCategoryViewState>> {
        return object :
            NetworkBoundResource<ListProductResponse, List<Product>, CustomCategoryViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListProductResponse>) {
                withContext(Dispatchers.Main){
                    Log.d(TAG, "handleApiSuccessResponse: ${response}")
                    Log.d(TAG,id.toString())
                    onCompleteJob(
                        DataState.data(
                            data = CustomCategoryViewState(
                                productList = ProductList(response.body.results)
                            ),
                            response = null
                        )
                    )
                }

            }


            override fun createCall(): LiveData<GenericApiResponse<ListProductResponse>> {
                return openApiMainService.getAllProductByCategory(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<CustomCategoryViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: List<Product>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptProductMain", job)
            }

        }.asLiveData()
    }
}
















