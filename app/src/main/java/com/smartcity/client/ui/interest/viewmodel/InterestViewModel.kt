package com.smartcity.client.ui.interest.viewmodel

import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.repository.interest.InterestRepositoryImpl
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.interest.state.InterestViewState
import com.smartcity.client.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@InterestScope
class InterestViewModel
@Inject
constructor(
    val interestRepository: InterestRepositoryImpl,
    private val sessionManager: SessionManager
): BaseViewModel<InterestViewState>()
{
    override fun handleNewData(data: InterestViewState) {
        data.categoryFields.let {categoryFields ->
            categoryFields.categoryList?.let {list ->
                setCategoryList(list)
            }

            categoryFields.userInterestList?.let {list ->
                setUserInterestList(list)
            }
        }

        data.configurationFields.let {configurationFields ->
            configurationFields.cityList?.let {list ->
                setCityList(list)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<InterestViewState>> = when(stateEvent){

                    is InterestStateEvent.UserInterestCenterEvent ->{
                        interestRepository.attemptUserInterestCenter(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is InterestStateEvent.SetInterestCenterEvent ->{
                        interestRepository.attemptSetInterestCenter(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.list
                        )
                    }

                    is InterestStateEvent.AllCategoryEvent ->{
                        interestRepository.attemptAllCategory(stateEvent)
                    }

                    is InterestStateEvent.ResolveUserAddressEvent ->{
                         interestRepository.attemptResolveUserAddress(
                             stateEvent,
                            getCountry(),
                            getCity()
                        )
                    }

                    is InterestStateEvent.SetUserDefaultCityEvent ->{
                        stateEvent.city.userId=authToken.account_pk!!.toLong()
                        interestRepository.attemptSetUserDefaultCity(
                            stateEvent,
                            stateEvent.city
                        )
                    }

                    is InterestStateEvent.CreateAddressEvent ->{
                        stateEvent.address.userId=authToken.account_pk!!.toLong()
                        interestRepository.attemptCreateAddress(
                            stateEvent,
                            stateEvent.address
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<InterestViewState>(
                                    response = Response(
                                        message = ErrorHandling.INVALID_STATE_EVENT,
                                        uiComponentType = UIComponentType.None(),
                                        messageType = MessageType.Error()
                                    ),
                                    stateEvent = stateEvent
                                )
                            )
                        }
                    }
                }
                launchJob(stateEvent, job)
            }
        }
    }

    override fun initNewViewState(): InterestViewState {
        return InterestViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}