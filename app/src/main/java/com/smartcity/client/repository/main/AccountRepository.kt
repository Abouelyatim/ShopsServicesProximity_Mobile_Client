package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.here.oksse.ServerSentEvent
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.ServerSentEventImpl
import com.smartcity.client.api.main.responses.ListAddressResponse
import com.smartcity.client.api.main.responses.ListOrderResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.Order
import com.smartcity.client.models.UserInformation
import com.smartcity.client.persistence.AccountPropertiesDao
import com.smartcity.client.repository.*
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Response
import com.smartcity.client.ui.ResponseType
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.client.util.*
import com.smartcity.client.util.SuccessHandling.Companion.DONE_ORDER_EVENT_CHANGE
import com.smartcity.client.util.SuccessHandling.Companion.MUST_UPDATE_UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Request
import javax.inject.Inject

@MainScope
class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
): JobManager("AccountRepository"),
    BaseRepository<AccountStateEvent, AccountViewState>
{
    lateinit var viewModel: AccountViewModel

    override fun setCurrentViewModel(viewModel: BaseViewModel<AccountStateEvent, AccountViewState>) {
        this.viewModel=viewModel as AccountViewModel
    }

    private val TAG: String = "AppDebug"


    fun attemptCreateAddress(
        address: Address
    ): LiveData<DataState<AccountViewState>> {

        val createAddressError=address.isValidForCreation()

        if(!createAddressError.equals(Address.CreateAddressError.none())){
            return returnErrorResponse(createAddressError, ResponseType.Dialog())

        }

        return object :
            NetworkBoundResource<GenericResponse, Any, AccountViewState>(
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

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptCreateAddress", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptUserAddresses(
        id: Long
    ): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<ListAddressResponse, Address, AccountViewState>(
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
                        data = AccountViewState(
                            addressList=response.body.results
                        ),
                        response = null
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListAddressResponse>> {
                return openApiMainService.getUserAddresses(
                    userId = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
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

    fun attemptDeleteAddress(
        id: Long
    ): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<GenericResponse, Address, AccountViewState>(
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

                if(response.body.response == SuccessHandling.DELETE_DONE){
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

                return openApiMainService.deleteUserAddress(
                    id=id
                )
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptDeleteAddress", job)
            }

            override suspend fun updateLocalDb(cacheObject: Address?) {

            }

        }.asLiveData()
    }

    fun attemptSetUserInformation(
        userInformation: UserInformation
    ): LiveData<DataState<AccountViewState>> {

        val createUserInformationError=userInformation.isValidForCreation()
        if(!createUserInformationError.equals(Address.CreateAddressError.none())){
            return returnErrorResponse(createUserInformationError, ResponseType.Dialog())

        }

        return object :
            NetworkBoundResource<GenericResponse, Any, AccountViewState>(
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

                return openApiMainService.setUserInformation(
                    userInformation = userInformation
                )
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptSetUserInformation", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptGetUserInformation(
        id:Long
    ): LiveData<DataState<AccountViewState>> {


        return object :
            NetworkBoundResource<UserInformation, Any, AccountViewState>(
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
                        data = AccountViewState(
                            userInformation=response.body
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

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptGetUserInformation", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptUserInProgressOrders(
        id: Long
    ): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<ListOrderResponse, Order, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListOrderResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = AccountViewState(
                            orderFields= AccountViewState.OrderFields(
                                ordersList = response.body.results
                            )
                        ),
                        response = Response(SuccessHandling.DONE_USER_ORDERS,ResponseType.None())
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListOrderResponse>> {
                return openApiMainService.getUserInProgressOrders(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Order?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptUserInProgressOrders", job)
            }

        }.asLiveData()
    }

    fun attemptUserFinalizedOrders(
        id: Long
    ): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<ListOrderResponse, Order, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListOrderResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = AccountViewState(
                            orderFields= AccountViewState.OrderFields(
                                ordersList = response.body.results
                            )
                        ),
                        response = Response(SuccessHandling.DONE_USER_ORDERS,ResponseType.None())
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListOrderResponse>> {
                return openApiMainService.getUserFinalizedOrders(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Order?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptUserFinalizedOrders", job)
            }

        }.asLiveData()
    }

    fun attemptConfirmOrderReceived(
        id:Long
    ): LiveData<DataState<AccountViewState>> {

        return object: NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = null,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            ResponseType.Toast()
                        )
                    )
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.confirmOrderReceived(
                    id= id
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

            override fun setJob(job: Job) {
                addJob("attemptConfirmOrderReceived", job)
            }


        }.asLiveData()
    }

    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<AccountViewState>>{
        Log.d(TAG, "returnErrorResponse: ${errorMessage}")

        return object: LiveData<DataState<AccountViewState>>(){
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





    lateinit var sse: ServerSentEvent
    fun attemptSubscribeOrderChangeEvent(
    ): LiveData<DataState<AccountViewState>>{
        Log.d(TAG, "attemptSubscribeOrderChangeEvent")
       /* val listener= object: ServerSentEvent.Listener{
            override fun onOpen(sse: ServerSentEvent?, response: okhttp3.Response?) {

            }

            override fun onRetryTime(sse: ServerSentEvent?, milliseconds: Long): Boolean {
                return true
            }

            override fun onComment(sse: ServerSentEvent?, comment: String?) {

            }

            override fun onRetryError(
                sse: ServerSentEvent?,
                throwable: Throwable?,
                response: okhttp3.Response?
            ): Boolean {
                return true
            }

            override fun onPreRetry(sse: ServerSentEvent?, originalRequest: Request?): Request {
                return originalRequest!!
            }

            override fun onMessage(
                sse: ServerSentEvent?,
                id: String?,
                event: String?,
                message: String?
            ) {
                attemptRepositoryResponseOrderChangeEvent()
            }

            override fun onClosed(sse: ServerSentEvent?) {

            }

        }

        sse= ServerSentEventImpl().getOrderChangeSSE(listener)*/

        return returnSettingsDone(null, Response(DONE_ORDER_EVENT_CHANGE,ResponseType.None()))
    }


    fun attemptFinishOrderChangeEvent(
    ): LiveData<DataState<AccountViewState>>{
        Log.d(TAG, "attemptSubscribeOrderChangeEvent")
       // sse.close()
        return returnSettingsDone(null,null)
    }

    fun attemptRepositoryResponseOrderChangeEvent(
    ) {
        Log.d(TAG, "attemptRepositoryResponseOrderChangeEvent")
        GlobalScope.launch(Dispatchers.Main){
            viewModel.setStateEvent(
                AccountStateEvent.ResponseOrderChangeEvent()
            )
        }
    }

    fun attemptResponseOrderChangeEvent(
    ): LiveData<DataState<AccountViewState>>{
        Log.d(TAG, "attemptResponseOrderChangeEvent")
        return returnSettingsDone(null,Response(MUST_UPDATE_UI,ResponseType.None()))
    }

    private fun returnSettingsDone(data:AccountViewState?,response: Response?): LiveData<DataState<AccountViewState>>{
        return object: LiveData<DataState<AccountViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(data, response)
            }
        }
    }
}












