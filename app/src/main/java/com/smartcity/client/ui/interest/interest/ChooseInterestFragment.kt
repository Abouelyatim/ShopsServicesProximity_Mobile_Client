package com.smartcity.client.ui.interest.interest

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartcity.client.R
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.ui.interest.BaseInterestFragment
import com.smartcity.client.ui.interest.InterestActivity
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.interest.viewmodel.getCategoryList
import com.smartcity.client.ui.interest.viewmodel.getSelectedCategoriesList
import com.smartcity.client.ui.interest.viewmodel.setSelectedCategoriesList
import com.smartcity.client.util.RetryToHandelNetworkError
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_choose_interest.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@InterestScope
class ChooseInterestFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseInterestFragment(R.layout.fragment_choose_interest,viewModelFactory),
    CategoriesAdapter.Interaction,
    RetryToHandelNetworkError
{
    private lateinit var recyclerAdapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as InterestActivity).initHandelNetworkError(this)

        //getUserInterestCenter()

        getAllCategory()
        subscribeObservers()
        initRecyclerView()
        saveInterestCenter()
    }


    override fun resendNetworkRequest() {
        getUserInterestCenter()
    }

    private fun getUserInterestCenter() {
        viewModel.setStateEvent(
            InterestStateEvent.UserInterestCenterEvent()
        )
    }


    private fun saveInterestCenter() {
        next_button_interest.setOnClickListener {
            viewModel.setStateEvent(
                InterestStateEvent.SetInterestCenterEvent(
                    viewModel.getSelectedCategoriesList()
                )
            )
        }
    }




    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must
            stateMessage?.let {


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

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerAdapter.submitList(viewModel.getCategoryList().distinct())
        })
    }

    fun initRecyclerView(){
        category_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ChooseInterestFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(5)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter =
                CategoriesAdapter(
                    this@ChooseInterestFragment
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

    private fun getAllCategory() {
        viewModel.setStateEvent(
            InterestStateEvent.AllCategoryEvent()
        )
    }

    override fun onItemSelected(option: String, value: String) {
        category_recyclerview.adapter!!.notifyDataSetChanged()
        val list=viewModel.getSelectedCategoriesList()
        if(list.contains(value)){
            list.removeAll { it == value }
        }else{
            list.add(value)
        }
        viewModel.setSelectedCategoriesList(list.distinct().toMutableList())
    }
}