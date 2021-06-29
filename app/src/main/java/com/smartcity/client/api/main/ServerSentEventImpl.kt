package com.smartcity.client.api.main

import com.here.oksse.OkSse
import com.here.oksse.ServerSentEvent
import com.smartcity.client.util.Constants.Companion.BASE_SSE_URL
import okhttp3.Request

class ServerSentEventImpl : ServerSentEvents {

    override fun getOrderChangeSSE(listener : ServerSentEvent.Listener): ServerSentEvent {
        val request: Request = Request.Builder().url("$BASE_SSE_URL/order-change-event").build()
        val okSse = OkSse()
        return okSse.newServerSentEvent(request, listener)
    }
}