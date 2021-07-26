package com.smartcity.client.ui.interest.state

import com.smartcity.client.models.Address
import com.smartcity.client.models.City

sealed class InterestStateEvent {

    class AllCategory:InterestStateEvent()

    class SetInterestCenter(
        val list: List<String>
    ):InterestStateEvent()

    class UserInterestCenter:InterestStateEvent()

    class ResolveUserAddress:InterestStateEvent()

    class SetUserDefaultCityEvent(
        val city: City
    ):InterestStateEvent()

    class CreateAddressEvent(
        val address: Address
    ):InterestStateEvent()

    class None: InterestStateEvent()
}