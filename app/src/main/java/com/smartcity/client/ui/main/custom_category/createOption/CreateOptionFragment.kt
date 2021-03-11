package com.smartcity.client.ui.main.custom_category.createOption

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.Attribute
import com.smartcity.client.models.product.AttributeValue
import com.smartcity.client.models.product.ProductVariantAttributeValue
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.client.ui.main.custom_category.CustomCategoryViewModel
import com.smartcity.client.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.util.ActionConstants
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_create_option.*
import javax.inject.Inject

class CreateOptionFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_create_option),
    OptionAdapter.Interaction
{
    private lateinit var recyclerAdapter: OptionAdapter
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

        createOptions()
        initvRecyclerView()
        subscribeObservers()
        adaptViewToNavigate()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerAdapter.submitList(viewModel.getOptionList())
        })
    }

    fun initvRecyclerView(){
        option_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@CreateOptionFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter =
                OptionAdapter(
                    this@CreateOptionFragment
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

    private fun createOptions() {
        add_create_option_button.setOnClickListener {
            findNavController().navigate(R.id.action_createOptionFragment_to_optionFragment)
        }
    }

    private fun deleteOption(attribute: Attribute){
        val attributeList=viewModel.getOptionList()
        attributeList.remove(attribute)
        viewModel.setOptionList(attributeList)
    }

    override fun onItemSelected(position: Int, item: Attribute, action: Int) {
        when(action){
            ActionConstants.DELETE ->{
                deleteOption(item)
            }

            ActionConstants.SELECTED ->{
                viewModel.setNewOption(item)
                findNavController().navigate(R.id.action_createOptionFragment_to_renameOptionFragment)
            }

            ActionConstants.UPDATE ->{
                viewModel.setNewOption(item)
                findNavController().navigate(R.id.action_createOptionFragment_to_optionValuesFragment)
            }
        }
    }

    val separator="/"
    private fun generateProductVariants() {
        viewModel.getOptionList()?.let {
            if(it.isNotEmpty()){
                val lists:MutableList<List<String>> = mutableListOf()
                it.map {
                    lists.add( it.attributeValues.map { it.value })
                }
                val result:MutableList<String> = mutableListOf()

                generatePermutation(lists,result,0,"")

                val productVariants: MutableList<ProductVariants> = mutableListOf()
                result.map {
                    val attrebuteValues=it.split(separator).toMutableList()
                    attrebuteValues.remove("")

                    val productVariantAttributeValues:MutableList<ProductVariantAttributeValue> = mutableListOf()
                    attrebuteValues.map {
                        productVariantAttributeValues.add(
                            ProductVariantAttributeValue(
                            AttributeValue(it,"")
                        )
                        )
                    }

                    productVariants.add(ProductVariants(productVariantAttributeValues,null,0.0,0,null))

                }
                viewModel.setProductVariantsList(productVariants)
            }else{
                viewModel.setProductVariantsList(mutableListOf())
            }

        }
    }

    private fun generatePermutation(lists:List<List<String>>,result:MutableList<String>,depth :Int, current:String){
        if(depth==lists.size){
            result.add(current)
            return
        }
        for (i in 0..lists.get(depth).size-1){
            generatePermutation(lists,result,depth+1,current+separator+lists.get(depth).get(i))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        viewModel.getOptionList()?.let {
            it.isNotEmpty()
        }?.let {
            if(it){
                inflater.inflate(R.menu.update_menu, menu)
            }
        }

    }

    fun adaptViewToNavigate(){
        activity?.invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                generateProductVariants()
                findNavController().navigate(R.id.action_createOptionFragment_to_createProductFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        generateProductVariants()
        option_recyclerview.adapter = null
    }
}