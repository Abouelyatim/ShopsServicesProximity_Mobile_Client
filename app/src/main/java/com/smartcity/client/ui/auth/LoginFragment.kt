package com.smartcity.client.ui.auth


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.R
import com.smartcity.client.di.auth.AuthScope
import com.smartcity.client.ui.auth.state.AuthStateEvent.LoginAttemptEvent
import com.smartcity.client.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AuthScope
class LoginFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
): BaseAuthFragment(R.layout.fragment_login,viewModelFactory) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        login_button.setOnClickListener {
            login()
        }
    }

    fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer{
            it.loginFields?.let{
                it.login_email?.let{input_email.setText(it)}
                it.login_password?.let{input_password.setText(it)}
            }
        })
    }

    fun login(){
        viewModel.setStateEvent(
            LoginAttemptEvent(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }
}
















