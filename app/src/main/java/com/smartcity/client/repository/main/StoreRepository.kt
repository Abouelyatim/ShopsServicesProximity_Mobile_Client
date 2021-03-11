package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.ListCustomCategoryResponse
import com.smartcity.client.api.main.responses.ListProductResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import com.smartcity.client.persistence.AccountPropertiesDao
import com.smartcity.client.repository.JobManager
import com.smartcity.client.repository.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Response
import com.smartcity.client.ui.ResponseType
import com.smartcity.client.ui.main.store.state.StoreViewState
import com.smartcity.client.util.AbsentLiveData
import com.smartcity.client.util.ApiSuccessResponse
import com.smartcity.client.util.GenericApiResponse
import com.smartcity.client.util.SuccessHandling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

@MainScope
class StoreRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
): JobManager("AccountRepository")
{

    private val TAG: String = "AppDebug"

    fun attemptCustomCategoryMain(
        id: Long
    ): LiveData<DataState<StoreViewState>> {
        return object :
            NetworkBoundResource<ListCustomCategoryResponse, List<CustomCategory>, StoreViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListCustomCategoryResponse>) {
                withContext(Dispatchers.Main){
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
                            data = StoreViewState(
                                viewCustomCategoryFields = StoreViewState.ViewCustomCategoryFields(customCategoryList)
                            ),
                            response = Response(
                                SuccessHandling.DONE_Custom_Category_Main,
                                ResponseType.None()
                            )
                        )
                    )
                }

            }


            override fun createCall(): LiveData<GenericApiResponse<ListCustomCategoryResponse>> {
                return openApiMainService.getAllcustomCategory(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<StoreViewState> {
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

    fun attemptProductMain(
        id: Long
    ): LiveData<DataState<StoreViewState>> {
        return object :
            NetworkBoundResource<ListProductResponse, List<Product>, StoreViewState>(
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
                            data = StoreViewState(
                                viewProductList = StoreViewState.ViewProductList(response.body.results)
                            ),
                            response = Response(
                                SuccessHandling.DONE_Product_Main,
                                ResponseType.None()
                            )
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
            override fun loadFromCache(): LiveData<StoreViewState> {
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

    fun attemptAllProduct(
        id: Long
    ): LiveData<DataState<StoreViewState>> {
        return object :
            NetworkBoundResource<ListProductResponse, List<Product>, StoreViewState>(
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
                            data = StoreViewState(
                                viewProductList = StoreViewState.ViewProductList(response.body.results)
                            ),
                            response = Response(
                                SuccessHandling.DONE_All_Product,
                                ResponseType.None()
                            )
                        )
                    )
                }

            }


            override fun createCall(): LiveData<GenericApiResponse<ListProductResponse>> {
                return openApiMainService.getAllProduct(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<StoreViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: List<Product>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptAllProduct", job)
            }

        }.asLiveData()
    }
}












