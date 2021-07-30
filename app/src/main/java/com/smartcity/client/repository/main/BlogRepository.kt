package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.ListGenericResponse
import com.smartcity.client.api.main.responses.ListProductResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.repository.deleted.JobManager
import com.smartcity.client.repository.deleted.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.deleted.DataState
import com.smartcity.client.ui.deleted.Response
import com.smartcity.client.ui.deleted.ResponseType
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.state.ProductViewState.ProductFields
import com.smartcity.client.util.*
import com.smartcity.client.util.deleted.AbsentLiveData
import com.smartcity.client.util.deleted.ApiSuccessResponse
import com.smartcity.client.util.deleted.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject

@MainScope
class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("BlogRepository")
{

    private val TAG: String = "AppDebug"

    fun searchBlogPosts(
        query: String,
        page: Int,
        userId: Long
    ): LiveData<DataState<ProductViewState>> {
        return object: NetworkBoundResource<ListProductResponse, List<Product>, ProductViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<ListProductResponse>
            ) {


                ProductViewState(
                    ProductFields(
                        newProductList =  response.body.results,
                        isQueryInProgress = false
                    )
                ).apply {
                    onCompleteJob(DataState.data(this, null))
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<ListProductResponse>> {
                return openApiMainService.searchListProduct(
                    query = query,
                    page = page,
                    userId =userId
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: List<Product>?) {

            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun attemptAddProductCart(
        userId: Long,
        variantId: Long,
        quantity: Int
    ): LiveData<DataState<ProductViewState>> {
        return object :
            NetworkBoundResource<GenericResponse, Any, ProductViewState>(
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

                if(response.body.response == SuccessHandling.SUCCESS_CREATED){
                    onCompleteJob(
                        DataState.data(
                            data = null
                            ,
                            response = Response(
                                SuccessHandling.DONE_ADD_TO_CART,
                                ResponseType.Toast()
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

                return openApiMainService.addProductCart(
                    userId,
                    variantId,
                    quantity
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptAddProductCart", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptGetStoreCustomCategory(
        storeId:Long
    ): LiveData<DataState<ProductViewState>> {
        return object: NetworkBoundResource<ListGenericResponse<CustomCategory>, List<CustomCategory>, ProductViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<ListGenericResponse<CustomCategory>>
            ) {
                onCompleteJob(
                    DataState.data(
                        data = ProductViewState(
                            viewProductFields = ProductViewState.ViewProductFields(
                                storeCustomCategoryList = response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Get_Store_Custom_Category,
                            ResponseType.None()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<CustomCategory>>> {
                return openApiMainService.getStoreCustomCategory(
                    storeId
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: List<CustomCategory>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptGetStoreCustomCategory", job)
            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun attemptGetProductsByCustomCategory(
        customCategoryId:Long
    ): LiveData<DataState<ProductViewState>> {
        return object: NetworkBoundResource<ListGenericResponse<Product>, List<Product>, ProductViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<ListGenericResponse<Product>>
            ) {

                onCompleteJob(
                    DataState.data(
                        data = ProductViewState(
                            viewProductFields = ProductViewState.ViewProductFields(
                                storeProductList = response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Get_Products_By_Custom_Category,
                            ResponseType.None()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<Product>>> {
                return openApiMainService.getProductsByCustomCategory(
                    customCategoryId
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: List<Product>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptGetProductsByCustomCategory", job)
            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun attemptGetAllProductsByStore(
        storeId:Long
    ): LiveData<DataState<ProductViewState>> {
        return object: NetworkBoundResource<ListGenericResponse<Product>, List<Product>, ProductViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<ListGenericResponse<Product>>
            ) {

                onCompleteJob(
                    DataState.data(
                        data = ProductViewState(
                            viewProductFields = ProductViewState.ViewProductFields(
                                storeProductList = response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Get_All_Products_By_Store,
                            ResponseType.None()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<Product>>> {
                return openApiMainService.getAllProductsByStore(
                    storeId
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: List<Product>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptGetAllProductsByStore", job)
            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun attemptFollowStore(
        id: Long,
        idUser: Long
    ): LiveData<DataState<ProductViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, ProductViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<GenericResponse>
            ) {

                onCompleteJob(
                    DataState.data(
                        data = null,
                        response = Response(
                            SuccessHandling.DONE_Follow_Store,
                            ResponseType.Dialog()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.followStore(
                    id,
                    idUser
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptFollowStore", job)
            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun attemptStopFollowingStore(
        id: Long,
        idUser: Long
    ): LiveData<DataState<ProductViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, ProductViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<GenericResponse>
            ) {

                onCompleteJob(
                    DataState.data(
                        data = null,
                        response = Response(
                            SuccessHandling.DONE_Stop_Following_Store,
                            ResponseType.None()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.stopFollowingStore(
                    id,
                    idUser
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptStopFollowingStore", job)
            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun attemptIsFollowingStore(
        id: Long,
        idUser: Long
    ): LiveData<DataState<ProductViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, ProductViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<GenericResponse>
            ) {

                onCompleteJob(
                    DataState.data(
                        data = null,
                        response = Response(
                            response.body.response,
                            ResponseType.None()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.isFollowingStore(
                    id,
                    idUser
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptIsFollowingStore", job)
            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun attemptInterestProduct(
        page: Int,
        userId: Long
    ): LiveData<DataState<ProductViewState>> {
        return object: NetworkBoundResource<ListProductResponse, List<Product>, ProductViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<ListProductResponse>
            ) {
                ProductViewState(
                    productInterestFields = ProductViewState.ProductInterestFields(
                        newProductList =  response.body.results,
                        isQueryInProgress = false
                    )

                ).apply {
                    onCompleteJob(DataState.data(this, null))
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<ListProductResponse>> {
                return openApiMainService.getInterestProduct(
                    page = page,
                    userId =userId
                )
            }

            override fun loadFromCache(): LiveData<ProductViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: List<Product>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptInterestProduct", job)
            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }
}
















