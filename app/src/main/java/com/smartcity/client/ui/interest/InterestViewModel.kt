package com.smartcity.client.ui.interest

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.models.product.Category
import com.smartcity.client.repository.interest.InterestRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Loading
import com.smartcity.client.ui.interest.state.CategoryFields
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.interest.state.InterestViewState
import com.smartcity.client.util.AbsentLiveData
import javax.inject.Inject

@InterestScope
class InterestViewModel
@Inject
constructor(
    val interestRepository: InterestRepository,
    private val sessionManager: SessionManager
): BaseViewModel<InterestStateEvent, InterestViewState>()
{
    override fun handleStateEvent(stateEvent: InterestStateEvent): LiveData<DataState<InterestViewState>> {
        when (stateEvent) {

            is InterestStateEvent.SetInterestCenter ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    interestRepository.attemptSetInterestCenter(
                        authToken.account_pk!!.toLong(),
                        stateEvent.list
                    )
                }?: AbsentLiveData.create()
            }
            is InterestStateEvent.AllCategory ->{
                return interestRepository.attemptAllCategory()
            }

            is InterestStateEvent.None ->{
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

    fun setCategoryList(categoryList:List<Category>){
        val update = getCurrentViewStateOrNew()
        update.categoryFields.categoryList = categoryList
        setViewState(update)
    }
    fun getCategoryList():List<Category>{
        getCurrentViewStateOrNew().let {
            return it.categoryFields.categoryList
        }
    }

    fun setSelectedCategoriesList(list: MutableList<String>){
        val update = getCurrentViewStateOrNew()
        update.categoryFields.selectedCategories = list
        setViewState(update)
    }

    fun getSelectedCategoriesList():MutableList<String>{
        getCurrentViewStateOrNew().let {
            return it.categoryFields.selectedCategories
        }
    }

    override fun initNewViewState(): InterestViewState {
        return InterestViewState()
    }

    fun cancelActiveJobs(){
        handlePendingData()
        interestRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(InterestStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}