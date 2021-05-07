package com.smartcity.client.ui.main.account.state

import com.smartcity.client.models.Address
import com.smartcity.client.models.UserInformation

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

    class None: AccountStateEvent()
}