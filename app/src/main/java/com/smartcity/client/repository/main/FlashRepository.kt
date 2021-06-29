package com.smartcity.client.repository.main

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.here.oksse.OkSse
import com.here.oksse.ServerSentEvent
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.ListGenericResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.FlashDeal
import com.smartcity.client.repository.BaseRepository
import com.smartcity.client.repository.JobManager
import com.smartcity.client.repository.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Response
import com.smartcity.client.ui.ResponseType
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.ui.main.flash_notification.viewmodel.FlashViewModel
import com.smartcity.client.util.AbsentLiveData
import com.smartcity.client.util.ApiSuccessResponse
import com.smartcity.client.util.GenericApiResponse
import kotlinx.coroutines.*
import okhttp3.Request
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

}