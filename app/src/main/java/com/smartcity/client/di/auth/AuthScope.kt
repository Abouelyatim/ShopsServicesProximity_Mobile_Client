package com.smartcity.client.di.auth

import javax.inject.Scope

/**
 * AuthScope is strictly for login and registration
 */
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class AuthScope