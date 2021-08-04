package com.smartcity.client.ui.main.account.information

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.smartcity.client.R
import com.smartcity.client.models.UserInformation
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.getUserInformation
import com.smartcity.client.util.DateUtils.Companion.convertLongToStringDate
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_information.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class InformationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_information,viewModelFactory){

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.hideSoftKeyboard()
        uiCommunicationListener.displayBottomNavigation(false)

        subscribeObservers()
        getUserInformation()

        input_birthday.setOnClickListener {
            showDatePicker()
        }

        save_information_button.setOnClickListener {
            saveUserInformation()
        }
    }

    private fun getUserInformation(){
        viewModel.setStateEvent(
            AccountStateEvent.GetUserInformationEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.CREATED_DONE)){
                    navAccount()
                }

                if(stateMessage.response.message.equals(SuccessHandling.CREATED_DONE)){
                    navAccount()
                }

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewModel.getUserInformation()?.let {
                initUi(it)
            }
        })
    }

    private fun initUi(userInformation: UserInformation){
        input_first_name.setText(userInformation.firstName)
        input_last_name.setText(userInformation.lastName)
        input_birthday.setText(userInformation.birthDay)
    }

    private fun navAccount(){
        findNavController().popBackStack()
    }

    private fun saveUserInformation() {
        viewModel.setStateEvent(
            AccountStateEvent.SetUserInformationEvent(
                UserInformation(
                    -1,
                    input_first_name.text.toString(),
                    input_last_name.text.toString(),
                    input_birthday.text.toString()
                )
            )
        )
    }

    private fun showDatePicker(){
        val builder= MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.select_date)
        builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar_Fullscreen)
        val materialDatePicker=builder.build()
        materialDatePicker.addOnPositiveButtonClickListener {
            input_birthday.setText(convertLongToStringDate(it!!))
        }
        materialDatePicker.show(requireActivity().supportFragmentManager,"DATE_PICKER")
    }
}