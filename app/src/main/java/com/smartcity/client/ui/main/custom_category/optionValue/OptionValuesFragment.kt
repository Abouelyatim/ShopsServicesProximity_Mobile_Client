package com.smartcity.client.ui.main.custom_category.optionValue

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.AttributeValue
import com.smartcity.client.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.client.ui.main.custom_category.CustomCategoryViewModel
import com.smartcity.client.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_option_values.*
import javax.inject.Inject


class OptionValuesFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_option_values),
    OptionValuesAdapter.Interaction
{
    private lateinit var recyclerAdapter: OptionValuesAdapter
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
        createOptionValue()
        initvRecyclerView()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerAdapter.submitList(viewModel.getNewOption()?.let {it.attributeValues })

            viewModel.getNewOption()?.let {
                if(it.attributeValues.isNotEmpty()){
                    adaptViewToNavigate()
                }
            }

        })
    }

    private fun createOptionValue() {
        add_option_value_name_button.setOnClickListener {
            val attrebute=viewModel.getNewOption()
            attrebute?.let {
                if (input_value.text.toString().isNotBlank().and(input_value.text.toString().isNotEmpty())){
                    Log.d("ii",viewModel.getOptionList().toString())
                    it.attributeValues.add(AttributeValue(input_value.text.toString(),""))
                    Log.d("ii",viewModel.getOptionList().toString())
                    viewModel.setNewOption(it)
                    input_value.text.clear()
                }
            }

        }
    }

    fun initvRecyclerView(){
        option_value_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@OptionValuesFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter =
                OptionValuesAdapter(
                    this@OptionValuesFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerAdapter
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        viewModel.getNewOption()?.let { it.attributeValues.isNotEmpty() }?.let {
            if(it){
                inflater.inflate(R.menu.update_menu, menu)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.getNewOption()?.let { it.attributeValues.isNotEmpty() }?.let {
            if(it){
                when(item.itemId){
                    R.id.save -> {
                        viewModel.getNewOption()?.let {
                            viewModel.setOptionList(it)
                        }
                        findNavController().navigate(R.id.action_optionValuesFragment_to_createOptionFragment)
                        return true
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun adaptViewToNavigate(){
        activity?.invalidateOptionsMenu()
    }

    override fun onItemSelected(position: Int, item: AttributeValue) {
        deleteOptionValue(item)
    }

    private fun deleteOptionValue(item: AttributeValue){
        val attrebute=viewModel.getNewOption()
        attrebute?.let {
            it.attributeValues.remove(item)
            viewModel.setNewOption(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        option_value_recyclerview.adapter = null
        viewModel.setNewOption(null)
    }
}