package com.smartcity.client.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.ListProductResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.product.Product
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.repository.JobManager
import com.smartcity.client.repository.NetworkBoundResource
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Response
import com.smartcity.client.ui.ResponseType
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.state.ProductViewState.ProductFields
import com.smartcity.client.util.*
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

}
















