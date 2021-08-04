package com.smartcity.client.ui.main.cart.information

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.smartcity.client.R
import com.smartcity.client.models.UserInformation
import com.smartcity.client.ui.main.cart.BaseCartFragment
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.viewmodel.setUserInformation
import com.smartcity.client.util.DateUtils
import kotlinx.android.synthetic.main.fragment_add_user_information.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AddUserInformationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCartFragment(R.layout.fragment_add_user_information,viewModelFactory)
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CartViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    /**
     * !IMPORTANT!
     * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.hideSoftKeyboard()
        uiCommunicationListener.displayBottomNavigation(false)

        input_birthday.setOnClickListener {
            showDatePicker()
        }

        save_information_button.setOnClickListener {
            saveUserInformation()
        }
    }

    private fun saveUserInformation() {
        val userInformation=
            UserInformation(
            -1,
            input_first_name.text.toString(),
            input_last_name.text.toString(),
            input_birthday.text.toString()
        )


        viewModel.setUserInformation(
            userInformation
        )
        findNavController().popBackStack()
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
}