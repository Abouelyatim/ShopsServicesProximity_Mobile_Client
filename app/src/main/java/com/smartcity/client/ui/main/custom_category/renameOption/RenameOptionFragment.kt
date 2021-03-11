package com.smartcity.client.ui.main.custom_category.renameOption

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.client.ui.main.custom_category.CustomCategoryViewModel
import com.smartcity.client.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import kotlinx.android.synthetic.main.fragment_rename_option.*
import javax.inject.Inject


class RenameOptionFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_rename_option)
{

    val viewModel: CustomCategoryViewModel by viewModels{
        viewModelFactory
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CustomCategoryViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }
    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        stateChangeListener.expandAppBar()


        viewModel.getNewOption()?.let {
            setOptionName(it.name)
        }


        adaptViewToNavigate()


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }
    private fun setOptionName(name:String){
        input_option_rename.setText(name)
    }
    fun adaptViewToNavigate(){
        activity?.invalidateOptionsMenu()
    }
    fun updateOptionName(){
        val option=(viewModel.getNewOption())

       val optionList= viewModel.getOptionList()
        optionList.find { it.name.equals(option!!.name) }!!
            .name=input_option_rename.text.toString()

        viewModel.setOptionList(optionList)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.getOptionList()?.let { it.isNotEmpty() }?.let {
            if(it){
                when(item.itemId){
                    R.id.save -> {
                        updateOptionName()
                        findNavController().navigate(R.id.action_renameOptionFragment_to_createOptionFragment)
                        return true
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setNewOption(null)
    }
}