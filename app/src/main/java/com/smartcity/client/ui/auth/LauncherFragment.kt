package com.smartcity.client.ui.auth


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.smartcity.client.R
import com.smartcity.client.di.auth.AuthScope
import kotlinx.android.synthetic.main.fragment_launcher.*
import javax.inject.Inject

    @AuthScope
    class LauncherFragment
    @Inject
    constructor(
        private val viewModelFactory: ViewModelProvider.Factory
    ): BaseAuthFragment(R.layout.fragment_launcher) {



        val viewModel: AuthViewModel by viewModels{
            viewModelFactory
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            viewModel.cancelActiveJobs()
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            register.setOnClickListener {
                navRegistration()
            }

            login.setOnClickListener {
                navLogin()
            }

            forgot_password.setOnClickListener {
                navForgotPassword()
            }

            focusable_view.requestFocus() // reset focus
        }

        override fun cancelActiveJobs() {
            viewModel.cancelActiveJobs()
        }

        fun navLogin(){
            findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
        }

        fun navRegistration(){
            findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
        }

        fun navForgotPassword(){
            findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
        }

    }





















