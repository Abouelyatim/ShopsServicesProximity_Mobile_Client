package com.smartcity.client.ui.interest.state

sealed class InterestStateEvent {

    class AllCategory:InterestStateEvent()

    class SetInterestCenter(
        val list: List<String>
    ):InterestStateEvent()

    class UserInterestCenter:InterestStateEvent()

    class None: InterestStateEvent()
}