package com.smartcity.client.ui.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smartcity.client.R
import com.smartcity.client.di.auth.AuthScope
import com.smartcity.client.ui.auth.state.AuthStateEvent.RegisterAttemptEvent
import com.smartcity.client.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AuthScope
class RegisterFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
): BaseAuthFragment(R.layout.fragment_register,viewModelFactory) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_button.setOnClickListener {
            register()
        }
        subscribeObservers()
    }

    fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer{viewState ->
            viewState.registrationFields?.let {
                it.registration_email?.let{input_email.setText(it)}
                it.registration_username?.let{input_username.setText(it)}
                it.registration_password?.let{input_password.setText(it)}
                it.registration_confirm_password?.let{input_password_confirm.setText(it)}
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if(viewModel.isRegistred()){
                navLauncherFragment()
            }
        })
    }

    fun navLauncherFragment(){
        findNavController().navigate(R.id.action_registerFragment_to_launcherFragment)
    }

    fun register(){
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
        viewModel.setIsRegistred(false)
        viewModel.setStateEvent(
            RegisterAttemptEvent(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setIsRegistred(false)
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }
}
