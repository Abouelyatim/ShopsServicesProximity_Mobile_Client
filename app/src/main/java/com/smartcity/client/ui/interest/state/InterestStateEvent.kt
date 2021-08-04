package com.smartcity.client.ui.interest.state

import com.smartcity.client.models.Address
import com.smartcity.client.models.City
import com.smartcity.client.models.UserInformation
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.util.StateEvent

sealed class InterestStateEvent: StateEvent {

    class AllCategoryEvent:InterestStateEvent() {
        override fun errorInfo(): String {
            return "Interest center attempt failed."
        }

        override fun toString(): String {
            return "AllCategoryStateEvent"
        }
    }

    class SetInterestCenterEvent(
        val list: List<String>
    ):InterestStateEvent() {
        override fun errorInfo(): String {
            return "Save interest center attempt failed."
        }

        override fun toString(): String {
            return "SetInterestCenterStateEvent"
        }
    }

    class UserInterestCenterEvent:InterestStateEvent() {
        override fun errorInfo(): String {
            return "Interest center attempt failed."
        }

        override fun toString(): String {
            return "UserInterestCenterStateEvent"
        }
    }

    class ResolveUserAddressEvent:InterestStateEvent() {
        override fun errorInfo(): String {
           return "Resolve address attempt failed."
        }

        override fun toString(): String {
            return "ResolveUserAddressStateEvent"
        }
    }

    class SetUserDefaultCityEvent(
        val city: City
    ):InterestStateEvent() {
        override fun errorInfo(): String {
            return "Save city attempt failed."
        }

        override fun toString(): String {
            return "SetUserDefaultCityStateEvent"
        }
    }

    class CreateAddressEvent(
        val address: Address
    ):InterestStateEvent() {
        override fun errorInfo(): String {
            return "Save address attempt failed."
        }

        override fun toString(): String {
            return "CreateAddressStateEvent"
        }
    }

    class SetUserInformationEvent(
        var userInformation: UserInformation?=null
    ): InterestStateEvent(){
        override fun errorInfo(): String {
            return "Save information attempt failed."
        }

        override fun toString(): String {
            return "SetUserInformationStateEvent"
        }
    }

    class None: InterestStateEvent() {
        override fun errorInfo(): String {
            return "None"
        }
    }
}