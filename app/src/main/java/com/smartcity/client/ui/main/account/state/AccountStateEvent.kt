package com.smartcity.client.ui.main.account.state

import com.smartcity.client.models.Address
import com.smartcity.client.models.UserInformation
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent

sealed class AccountStateEvent{
    class SaveAddress(
        var address: Address
    ):AccountStateEvent()

    class GetUserAddresses():AccountStateEvent()

    class DeleteAddress(
        var id:Long
    ):AccountStateEvent()

    class SetUserInformation(
        var userInformation: UserInformation
    ):AccountStateEvent()

    class GetUserInformation(
    ):AccountStateEvent()

    class GetUserInProgressOrdersEvent():AccountStateEvent()

    class GetUserFinalizedOrdersEvent():AccountStateEvent()

    class ConfirmOrderReceivedEvent(
        var id:Long
    ) : AccountStateEvent()

    class SubscribeOrderChangeEvent: AccountStateEvent()

    class FinishOrderChangeEvent: AccountStateEvent()

    class ResponseOrderChangeEvent: AccountStateEvent()

    class None: AccountStateEvent()
}