package com.smartcity.client.ui.interest.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.models.product.Category
import com.smartcity.client.repository.interest.InterestRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Loading
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

            is InterestStateEvent.UserInterestCenter ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    interestRepository.attemptUserInterestCenter(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

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

            is InterestStateEvent.ResolveUserAddress ->{
                return interestRepository.attemptResolveUserAddress(
                    getCountry(),
                    getCity()
                )
            }

            is InterestStateEvent.SetUserDefaultCityEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.city.userId=authToken.account_pk!!.toLong()
                    interestRepository.attemptSetUserDefaultCity(
                        stateEvent.city
                    )
                }?: AbsentLiveData.create()
            }

            is InterestStateEvent.CreateAddressEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.address.userId=authToken.account_pk!!.toLong()
                    interestRepository.attemptCreateAddress(
                        stateEvent.address
                    )
                }?: AbsentLiveData.create()
            }

            is InterestStateEvent.None ->{
                return liveData {
                    emit(
                        DataState<InterestViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
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

    override fun initRepositoryViewModel() {

    }
}