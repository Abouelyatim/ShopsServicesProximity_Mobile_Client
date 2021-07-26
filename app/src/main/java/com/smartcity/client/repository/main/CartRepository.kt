package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.ListAddressResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.BillTotal
import com.smartcity.client.models.Order
import com.smartcity.client.models.UserInformation
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
import com.smartcity.client.util.SuccessHandling.Companion.DONE_STORE_POLICY
import com.smartcity.client.util.SuccessHandling.Companion.DONE_TOTAL_BILL
import com.smartcity.client.util.SuccessHandling.Companion.DONE_USER_ADDRESSES
import com.smartcity.provider.models.Policy
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
                        response = Response(
                            SuccessHandling.DONE_USER_CART,
                            ResponseType.None()
                        )
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


    fun attemptPlaceOrder(
        order: Order
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
                                SuccessHandling.DONE_PLACE_ORDER,
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

                return openApiMainService.placeOrder(
                    order
                )
            }

            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptPlaceOrder", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptStorePolicy(
        storeId: Long
    ): LiveData<DataState<CartViewState>> {
        return object :
            NetworkBoundResource<Policy, Policy, CartViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Policy>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = CartViewState(
                            orderFields = OrderFields(
                                storePolicy = response.body
                            )
                        ),
                        response = Response(DONE_STORE_POLICY,ResponseType.None())
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<Policy>> {
                return openApiMainService.getStorePolicy(
                    storeId = storeId
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Policy?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptStorePolicy", job)
            }

        }.asLiveData()
    }

    fun attemptTotalBill(
        bill: BillTotal
    ): LiveData<DataState<CartViewState>> {
        return object :
            NetworkBoundResource<BillTotal, BillTotal, CartViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BillTotal>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = CartViewState(
                            orderFields = OrderFields(
                                total = response.body
                            )
                        ),
                        response = Response(DONE_TOTAL_BILL,ResponseType.None())
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<BillTotal>> {
                return openApiMainService.getTotalBill(
                    bill = bill
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: BillTotal?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptTotalBill", job)
            }

        }.asLiveData()
    }

    fun attemptUserAddresses(
        id: Long
    ): LiveData<DataState<CartViewState>> {
        return object :
            NetworkBoundResource<ListAddressResponse, Address, CartViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListAddressResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = CartViewState(
                            orderFields=OrderFields(addressList=response.body.results)
                        ),
                        response = Response(DONE_USER_ADDRESSES,ResponseType.None())
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListAddressResponse>> {
                return openApiMainService.getUserAddresses(
                    userId = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Address?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptUserAddresses", job)
            }

        }.asLiveData()
    }

    fun attemptCreateAddress(
        address: Address
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


                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.CREATED_DONE,
                            ResponseType.Toast()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {

                return openApiMainService.createAddress(
                    address = address
                )
            }

            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptCreateAddress", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptGetUserInformation(
        id:Long
    ): LiveData<DataState<CartViewState>> {


        return object :
            NetworkBoundResource<UserInformation, Any, CartViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<UserInformation>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = CartViewState(
                            orderFields = OrderFields(
                                userInformation = response.body
                            )
                        )
                        ,
                        response = Response(
                            SuccessHandling.DONE_USER_INFORMATION,
                            ResponseType.None()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<UserInformation>> {

                return openApiMainService.getUserInformation(
                    id = id
                )
            }

            override fun loadFromCache(): LiveData<CartViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptGetUserInformation", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<CartViewState>>{
        Log.d(TAG, "returnErrorResponse: ${errorMessage}")

        return object: LiveData<DataState<CartViewState>>(){
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
}
















