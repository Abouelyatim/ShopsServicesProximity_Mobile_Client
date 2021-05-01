package com.smartcity.client.ui.main.account.state

import com.smartcity.client.models.Address

sealed class AccountStateEvent{
    class SaveAddress(
        var address: Address
    ):AccountStateEvent()

    class GetUserAddresses():AccountStateEvent()

    class DeleteAddress(
        var id:Long
    ):AccountStateEvent()

    class None: AccountStateEvent()
}