package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.util.ListGenericResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product
import com.smartcity.client.repository.deleted.JobManager
import com.smartcity.client.repository.deleted.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.deleted.DataState
import com.smartcity.client.ui.deleted.Response
import com.smartcity.client.ui.deleted.ResponseType
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.util.*
import com.smartcity.client.util.deleted.AbsentLiveData
import com.smartcity.client.util.deleted.ApiSuccessResponse
import com.smartcity.client.util.deleted.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject

@MainScope
class FlashRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager
): JobManager("FlashRepository"){
    private val TAG: String = "AppDebug"

    fun attemptUserFlashDeals(
        id: Long
    ): LiveData<DataState<FlashViewState>> {
        return object :
            NetworkBoundResource<ListGenericResponse<FlashDeal>, FlashDeal, FlashViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListGenericResponse<FlashDeal>>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = FlashViewState(
                            flashFields = FlashViewState.FlashFields(
                                flashDealsList = response.body.results
                            )
                        ),
                        response = null
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<FlashDeal>>> {
                return openApiMainService.getUserFlashDeals(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<FlashViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: FlashDeal?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptUserFlashDeals", job)
            }

        }.asLiveData()
    }

    fun attemptUserDiscountProduct(
        id: Long
    ): LiveData<DataState<FlashViewState>> {
        return object :
            NetworkBoundResource<ListGenericResponse<Product>, Product, FlashViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListGenericResponse<Product>>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = FlashViewState(
                            flashFields = FlashViewState.FlashFields(
                                productDiscountList = response.body.results
                            )
                        ),
                        response = null
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<Product>>> {
                return openApiMainService.getUserDiscountProduct(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<FlashViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Product?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptUserDiscountProduct", job)
            }

        }.asLiveData()
    }

    fun attemptAddProductCart(
        userId: Long,
        variantId: Long,
        quantity: Int
    ): LiveData<DataState<FlashViewState>> {
        return object :
            NetworkBoundResource<GenericResponse, Any, FlashViewState>(
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

            override fun loadFromCache(): LiveData<FlashViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptAddProductCart", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }
}