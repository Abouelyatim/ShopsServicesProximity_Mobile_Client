package com.smartcity.client.ui.interest.state

sealed class InterestStateEvent {

    class AllCategory:InterestStateEvent()

    class None: InterestStateEvent()
}