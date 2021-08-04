package com.smartcity.client.repository.interest

import com.smartcity.client.di.auth.AuthScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.City
import com.smartcity.client.models.UserInformation
import com.smartcity.client.ui.interest.state.InterestViewState
import com.smartcity.client.util.DataState
import com.smartcity.client.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@AuthScope
interface InterestRepository {

    fun attemptAllCategory(
        stateEvent: StateEvent
    ): Flow<DataState<InterestViewState>>

    fun attemptSetInterestCenter(
        stateEvent: StateEvent,
        id:Long,
        interest: List<String>
    ): Flow<DataState<InterestViewState>>

    fun attemptUserInterestCenter(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<InterestViewState>>

    fun attemptResolveUserAddress(
        stateEvent: StateEvent,
        country:String,
        city: String
    ): Flow<DataState<InterestViewState>>

    fun attemptCreateAddress(
        stateEvent: StateEvent,
        address: Address
    ): Flow<DataState<InterestViewState>>

    fun attemptSetUserDefaultCity(
        stateEvent: StateEvent,
        city: City
    ): Flow<DataState<InterestViewState>>

    fun attemptSetUserInformation(
        stateEvent: StateEvent,
        userInformation: UserInformation
    ): Flow<DataState<InterestViewState>>
}