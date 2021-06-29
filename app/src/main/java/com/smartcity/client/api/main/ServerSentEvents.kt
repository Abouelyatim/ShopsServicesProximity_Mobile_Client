package com.smartcity.client.api.main

import com.here.oksse.ServerSentEvent

interface ServerSentEvents {
    fun getOrderChangeSSE(listener : ServerSentEvent.Listener): ServerSentEvent
}