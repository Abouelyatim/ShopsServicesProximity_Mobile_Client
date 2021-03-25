package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.product.Cart
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.repository.JobManager
import com.smartcity.client.repository.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Response
import com.smartcity.client.ui.ResponseType
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.state.CartViewState.*
import com.smartcity.client.util.*
import com.smartcity.client.util.SuccessHandling.Companion.DELETE_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

@MainScope
class CartRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("CustomCategoryRepository") {

    private val TAG: String = "AppDebug"

    fun attemptUserCart(
        id: Long
    ): LiveData<DataState<CartViewState>> {
        return object :
            NetworkBoundResource<Cart, Cart, CartViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Cart>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = CartViewState(
                            cartFields = CartFields(
                                cartList = response.body
                            )
                        ),
                        response = null
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<Cart>> {
                return openApiMainService.getUserCart(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Cart?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptUserCart", job)
            }

        }.asLiveData()
    }

    fun attemptAddProductCart(
        userId: Long,
        variantId: Long,
        quantity: Int
    ): LiveData<DataState<CartViewState>> {
        return object :
            NetworkBoundResource<GenericResponse, Any, CartViewState>(
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
                                SuccessHandling.DONE_UPDATE_CART_QUANTITY,
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

            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptAddProductCart", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptDeleteProductCart(
        userId: Long,
        variantId: Long
    ): LiveData<DataState<CartViewState>> {
        return object :
            NetworkBoundResource<GenericResponse, Cart, CartViewState>(
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

                return openApiMainService.deleteProductCart(
                    userId,
                    variantId
                )
            }

            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptDeleteProductCart", job)
            }

            override suspend fun updateLocalDb(cacheObject: Cart?) {

            }

        }.asLiveData()
    }

}
















