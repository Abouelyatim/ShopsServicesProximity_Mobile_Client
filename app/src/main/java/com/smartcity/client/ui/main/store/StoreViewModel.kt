package com.smartcity.client.ui.main.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import com.smartcity.client.repository.main.StoreRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Loading
import com.smartcity.client.ui.main.store.state.StoreStateEvent
import com.smartcity.client.ui.main.store.state.StoreStateEvent.*
import com.smartcity.client.ui.main.store.state.StoreViewState
import com.smartcity.client.util.AbsentLiveData
import javax.inject.Inject

@MainScope
class StoreViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val storeRepository: StoreRepository
)
    : BaseViewModel<StoreStateEvent, StoreViewState>()
{
    override fun handleStateEvent(stateEvent: StoreStateEvent): LiveData<DataState<StoreViewState>> {
        when(stateEvent){

            is CustomCategoryMain ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    storeRepository.attemptCustomCategoryMain(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()

            }

            is ProductMain ->{
                return storeRepository.attemptProductMain(
                    stateEvent.id
                )
            }

            is AllProduct ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    storeRepository.attemptAllProduct(
                        authToken.account_pk!!.toLong()
                    )
                } ?: AbsentLiveData.create()

            }
            is None ->{
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }



    override fun initNewViewState(): StoreViewState {
        return StoreViewState()
    }

    fun clearViewProductList(){
        val update = getCurrentViewStateOrNew()
        update.viewProductList= StoreViewState.ViewProductList()
        setViewState(update)
    }
    fun setViewProductList(productList: StoreViewState.ViewProductList){
        val update = getCurrentViewStateOrNew()
        update.viewProductList=productList
        setViewState(update)
    }

    fun getViewProductList(): StoreViewState.ViewProductList {
        getCurrentViewStateOrNew().let {
            return it.viewProductList
        }
    }

    fun setViewCustomCategoryFields(viewCustomCategoryFields: StoreViewState.ViewCustomCategoryFields){
        val update = getCurrentViewStateOrNew()

        if(update.viewCustomCategoryFields == viewCustomCategoryFields){
            return
        }
        update.viewCustomCategoryFields = viewCustomCategoryFields
        setViewState(update)
    }
    fun getViewCustomCategoryFields():List<CustomCategory>{
        getCurrentViewStateOrNew().let {
            return it.viewCustomCategoryFields.customCategoryList
        }
    }

    fun setViewProductFields(product: Product){
        val update = getCurrentViewStateOrNew()
        update.viewProductFields.product = product
        setViewState(update)
    }

    fun getViewProductFields():Product?{
        getCurrentViewStateOrNew().let {
            return it.viewProductFields.product
        }
    }

    fun setChoisesMap(map: MutableMap<String, String>){
        val update = getCurrentViewStateOrNew()
        update.choisesMap.choises = map
        setViewState(update)
    }

    fun getChoisesMap():MutableMap<String, String>{
        getCurrentViewStateOrNew().let {
            return it.choisesMap.choises
        }
    }
    fun clearChoisesMap(){
        val update = getCurrentViewStateOrNew()
        update.choisesMap= StoreViewState.ChoisesMap()
        setViewState(update)
    }

    fun setCustomCategoryRecyclerPosition(postion:Int){
        val update = getCurrentViewStateOrNew()
        update.customCategoryRecyclerPosition=postion
        setViewState(update)
    }

    fun getCustomCategoryRecyclerPosition():Int{
        getCurrentViewStateOrNew().let {
            return it.customCategoryRecyclerPosition
        }
    }



    fun cancelActiveJobs(){
        storeRepository.cancelActiveJobs() // cancel active jobs
        handlePendingData() // hide progress bar
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}














