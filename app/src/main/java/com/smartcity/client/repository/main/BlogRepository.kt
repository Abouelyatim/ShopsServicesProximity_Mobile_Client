package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.BlogCreateUpdateResponse
import com.smartcity.client.api.main.responses.BlogListSearchResponse
import com.smartcity.client.api.main.responses.ListProductResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.AuthToken
import com.smartcity.client.models.BlogPost
import com.smartcity.client.models.product.Product
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.persistence.returnOrderedBlogQuery
import com.smartcity.client.repository.JobManager
import com.smartcity.client.repository.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Response
import com.smartcity.client.ui.ResponseType
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.state.ProductViewState.*
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.ui.main.store.state.StoreViewState
import com.smartcity.client.util.*
import com.smartcity.client.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.smartcity.client.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.smartcity.client.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.smartcity.client.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.smartcity.client.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        page: Int
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
                    page = page
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




}
















