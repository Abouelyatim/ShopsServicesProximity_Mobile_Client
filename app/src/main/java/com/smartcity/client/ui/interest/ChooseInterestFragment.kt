package com.smartcity.client.ui.interest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartcity.client.R
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.ui.auth.BaseAuthFragment
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.main.custom_category.customCategory.CustomCategoryAdapter
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_choose_interest.*
import kotlinx.android.synthetic.main.fragment_custom_category.*
import javax.inject.Inject

@InterestScope
class ChooseInterestFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseAuthFragment(R.layout.fragment_choose_interest),
    CategoriesAdapter.Interaction{

    private lateinit var recyclerAdapter: CategoriesAdapter

    val viewModel: InterestViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAllCategory()
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)

            //set  Category list get it from network
            dataState.data?.let { data ->
                data.data?.let{
                    it.getContentIfNotHandled()?.let{
                        viewModel.setCategoryList(it.categoryFields.categoryList)
                    }

                }

            }
        })
        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerAdapter.submitList(viewModel.getCategoryList().distinct())
        })
    }

    fun initRecyclerView(){
        category_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ChooseInterestFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
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
            InterestStateEvent.AllCategory()
        )
    }


    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }


    override fun onItemSelected(option: String, value: String) {
        category_recyclerview.adapter!!.notifyDataSetChanged()
        val map=
            updateSelectedMap(
            viewModel.getSelectedCategoriesMap(),
            option,
            value)

        viewModel.setSelectedCategoriesMap(map)
    }

    private fun updateSelectedMap(map:MutableMap<String, MutableList<String>>,option: String, value: String):MutableMap<String, MutableList<String>>{
        if(map[option]==null){
            map[option] = mutableListOf()
        }
        if ( map[option]!!.contains(value)){
            map[option]!!.removeAll { it == value }
            if(map[option]!!.isEmpty()){
                map.remove(option)
            }
        }else{
            map[option]!!.add(value)
        }
        return map
    }
}