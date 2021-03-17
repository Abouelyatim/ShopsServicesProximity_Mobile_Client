package com.smartcity.client.repository.interest

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.interest.OpenApiInterestService
import com.smartcity.client.api.interest.response.ListCategoryResponse
import com.smartcity.client.api.main.responses.ListCustomCategoryResponse
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.models.AuthToken
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Category
import com.smartcity.client.persistence.AuthTokenDao
import com.smartcity.client.repository.JobManager
import com.smartcity.client.repository.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Response
import com.smartcity.client.ui.ResponseType
import com.smartcity.client.ui.auth.state.LoginFields
import com.smartcity.client.ui.interest.state.CategoryFields
import com.smartcity.client.ui.interest.state.InterestViewState
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.util.*
import com.smartcity.client.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.smartcity.client.util.SuccessHandling.Companion.DONE_User_Interest_Center
import kotlinx.coroutines.Job
import javax.inject.Inject

@InterestScope
class InterestRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val openApiInterestService: OpenApiInterestService,
    val sessionManager: SessionManager
): JobManager("InterestRepository")
{
    private val TAG: String = "AppDebug"

    fun attemptAllCategory(
    ): LiveData<DataState<InterestViewState>> {
        return object :
            NetworkBoundResource<ListCategoryResponse, List<Category>, InterestViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListCategoryResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = InterestViewState(
                            categoryFields = CategoryFields(response.body.results)
                        ),
                        response = null
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListCategoryResponse>> {
                return openApiInterestService.getAllCategory()
            }

            // Ignore
            override fun loadFromCache(): LiveData<InterestViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: List<Category>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptAllCategory", job)
            }

        }.asLiveData()
    }

    fun attemptSetInterestCenter(
        id:Long,
        interest: List<String>
    ): LiveData<DataState<InterestViewState>> {

        val selectedCategoriesError = CategoryFields(
            listOf(),
            interest.toMutableList()
            ).isValid()

        if(!selectedCategoriesError.equals(CategoryFields.SelectedCategoriesError.none())){
            return returnErrorResponse(selectedCategoriesError, ResponseType.Dialog())
        }

        return object :
            NetworkBoundResource<GenericResponse, Category, InterestViewState>(
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

                if (response.body.response == SuccessHandling.CREATED_DONE){
                    // will return -1 if failure
                    val result = authTokenDao.insert(
                        AuthToken(
                            sessionManager.cachedToken.value!!.account_pk,
                            sessionManager.cachedToken.value!!.token,
                            true
                        )
                    )
                    if(result < 0){
                        return onCompleteJob(DataState.error(
                            Response(ErrorHandling.ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog()))
                        )
                    }

                    onCompleteJob(
                        DataState.data(
                            data = null
                            ,
                            response = Response(
                                SuccessHandling.CREATED_DONE,
                                ResponseType.None()
                            )
                        )
                    )
                }else{
                    onCompleteJob(
                        DataState.data(
                            data = null
                            ,
                            response = Response(
                                GENERIC_AUTH_ERROR,
                                ResponseType.None()
                            )
                        )
                    )
                }


            }


            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiInterestService.setInterestCenter(
                    id,
                    interest
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<InterestViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Category?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptSetInterestCenter", job)
            }

        }.asLiveData()
    }

    fun attemptUserInterestCenter(
        id: Long
    ): LiveData<DataState<InterestViewState>> {
        sessionManager.cachedToken.value?.let {
            it.interest
        }

        Log.d("ii",sessionManager.cachedToken.value!!.interest!!.toString())
        if(sessionManager.cachedToken.value!!.interest!!){//if interest is set so do not do request
            return returnErrorResponse("", ResponseType.None())
        }

        return object :
            NetworkBoundResource<ListCategoryResponse, List<Category>, InterestViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListCategoryResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if (response.body.results.isNotEmpty()){
                    val result = authTokenDao.insert(
                        AuthToken(
                            sessionManager.cachedToken.value!!.account_pk,
                            sessionManager.cachedToken.value!!.token,
                            true
                        )
                    )
                    if(result < 0){
                        return onCompleteJob(DataState.error(
                            Response(ErrorHandling.ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog()))
                        )
                    }
                }

                onCompleteJob(
                    DataState.data(
                        data = InterestViewState(
                            categoryFields = CategoryFields(response.body.results)
                        ),
                        response =  Response(
                            DONE_User_Interest_Center,
                            ResponseType.None()
                        )
                    )

                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListCategoryResponse>> {
                return openApiInterestService.getUserInterestCenter(
                    id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<InterestViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: List<Category>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptUserInterestCenter", job)
            }

        }.asLiveData()
    }


    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<InterestViewState>>{
        Log.d(TAG, "returnErrorResponse: ${errorMessage}")

        return object: LiveData<DataState<InterestViewState>>(){
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