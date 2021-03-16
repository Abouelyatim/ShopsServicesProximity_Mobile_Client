package com.smartcity.client.repository.interest

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.interest.OpenApiInterestService
import com.smartcity.client.api.interest.response.ListCategoryResponse
import com.smartcity.client.api.main.responses.ListCustomCategoryResponse
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Category
import com.smartcity.client.repository.JobManager
import com.smartcity.client.repository.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.interest.state.CategoryFields
import com.smartcity.client.ui.interest.state.InterestViewState
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.util.AbsentLiveData
import com.smartcity.client.util.ApiSuccessResponse
import com.smartcity.client.util.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject

@InterestScope
class InterestRepository
@Inject
constructor(
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
}