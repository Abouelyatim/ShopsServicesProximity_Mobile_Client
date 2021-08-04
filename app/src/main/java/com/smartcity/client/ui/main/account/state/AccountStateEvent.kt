package com.smartcity.client.ui.main.account.state

import com.smartcity.client.models.Address
import com.smartcity.client.models.UserInformation
import com.smartcity.client.util.StateEvent

sealed class AccountStateEvent: StateEvent {
    
    class SaveAddressEvent(
        var address: Address
    ):AccountStateEvent() {
        override fun errorInfo(): String {
            return "Save address attempt failed."
        }

        override fun toString(): String {
            return "SaveAddressStateEvent"
        }
    }

    class GetUserAddressesEvent(): AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get addresses attempt failed."
        }

        override fun toString(): String {
            return "GetUserAddressesStateEvent"
        }
    }

    class DeleteAddressEvent(
        var id:Long
    ):AccountStateEvent(){
        override fun errorInfo(): String {
            return "Delete address attempt failed."
        }

        override fun toString(): String {
            return "DeleteAddressStateEvent"
        }
    }

    class SetUserInformationEvent(
        var userInformation: UserInformation
    ):AccountStateEvent(){
        override fun errorInfo(): String {
            return "Save information attempt failed."
        }

        override fun toString(): String {
            return "SetUserInformationStateEvent"
        }
    }

    class GetUserInformationEvent():AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get information attempt failed."
        }

        override fun toString(): String {
            return "GetUserInformationStateEvent"
        }
    }

    class GetUserInProgressOrdersEvent():AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get orders attempt failed."
        }

        override fun toString(): String {
            return "GetUserInProgressOrdersStateEvent"
        }
    }

    class GetUserFinalizedOrdersEvent():AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get orders attempt failed."
        }

        override fun toString(): String {
            return "GetUserFinalizedOrdersStateEvent"
        }
    }

    class ConfirmOrderReceivedEvent(
        var id:Long
    ) : AccountStateEvent(){
        override fun errorInfo(): String {
            return "Confirm order attempt failed."
        }

        override fun toString(): String {
            return "ConfirmOrderReceivedStateEvent"
        }
    }

    class GetStoresAroundEvent: AccountStateEvent(){
        override fun errorInfo(): String {
            return "Get stores attempt failed."
        }

        override fun toString(): String {
            return "GetStoresAroundStateEvent"
        }
    }

    class None: AccountStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}