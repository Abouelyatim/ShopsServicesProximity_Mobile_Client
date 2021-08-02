package com.smartcity.client.api

interface GenericDto<T> {
    fun toModel():T
}