package com.smartcity.client.ui.interest.information

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.smartcity.client.R
import com.smartcity.client.models.UserInformation
import com.smartcity.client.ui.interest.BaseInterestFragment
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.interest.viewmodel.getBirthDay
import com.smartcity.client.ui.interest.viewmodel.getFirstName
import com.smartcity.client.ui.interest.viewmodel.getLastName
import com.smartcity.client.util.DateUtils
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_user_information.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class UserInformationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseInterestFragment(R.layout.fragment_user_information,viewModelFactory)
{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationButtons()
        subscribeObservers()

        input_birthday.setOnClickListener {
            showDatePicker()
        }
        saveUserInformation()
    }

    private fun saveUserInformation() {
        user_information_next_button.setOnClickListener {
            viewModel.setStateEvent(
                InterestStateEvent.SetUserInformationEvent(
                    UserInformation(
                        -1,
                        input_first_name.text.toString(),
                        input_last_name.text.toString(),
                        input_birthday.text.toString()
                    )
                )
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(InterestStateEvent.SetUserInformationEvent().toString())){
                    navInterest()
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
            viewModel.getFirstName().let {
                input_first_name.setText(it)
            }

            viewModel.getLastName().let {
                input_last_name.setText(it)
            }

            viewModel.getBirthDay().let {
                input_birthday.setText(it)
            }
        })
    }

    private fun navigationButtons() {
        skip_button.setOnClickListener {
            navInterest()
        }
    }

    private fun showDatePicker(){
        val builder= MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.select_date)
        builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar_Fullscreen)
        val materialDatePicker=builder.build()
        materialDatePicker.addOnPositiveButtonClickListener {
            input_birthday.setText(DateUtils.convertLongToStringDate(it!!))
        }
        materialDatePicker.show(requireActivity().supportFragmentManager,"DATE_PICKER")
    }

    private fun navInterest(){
        findNavController().navigate(R.id.action_userInformationFragment_to_chooseInterestFragment)
    }
}