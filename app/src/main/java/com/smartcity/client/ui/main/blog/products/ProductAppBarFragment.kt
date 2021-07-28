package com.smartcity.client.ui.main.blog.products

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.ui.main.blog.state.ProductStateEvent
import com.smartcity.client.ui.main.blog.viewmodel.ProductViewModel
import com.smartcity.client.ui.main.blog.viewmodel.getDummyBlogPost
import com.smartcity.client.ui.main.blog.viewmodel.getGridOrListView
import com.smartcity.client.ui.main.blog.viewmodel.setGridOrListView
import com.smartcity.client.util.SuccessHandling.Companion.DONE_search_Product_Event
import kotlinx.android.synthetic.main.fragment_product_app_bar.*
import javax.inject.Inject


class ProductAppBarFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): Fragment() {

    val viewModel: ProductViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_app_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGridListViewBehavior()
        initGridListViewBehavior(viewModel.getGridOrListView())
        searchProduct()
    }

    private fun searchProduct() {
        search_product.setOnClickListener {
            searchProductEvent()
        }
    }

    private fun searchProductEvent(){
        viewModel.setStateEvent(
            ProductStateEvent.SearchEvent(
                DONE_search_Product_Event
            )
        )
    }

    private fun initGridListViewBehavior(boolean: Boolean) {
        if(boolean){
            list_view_button.visibility =View.GONE
            grid_view_button.visibility=View.VISIBLE
        }else{
            grid_view_button.visibility=View.GONE
            list_view_button.visibility =View.VISIBLE
        }
    }

    private fun setGridListViewBehavior() {
        list_view_button.setOnClickListener {
            list_view_button.visibility =View.GONE
            grid_view_button.visibility=View.VISIBLE
            viewModel.setGridOrListView(true)
            layoutChangeEvent()
        }

        grid_view_button.setOnClickListener {
            grid_view_button.visibility=View.GONE
            list_view_button.visibility =View.VISIBLE
            viewModel.setGridOrListView(false)
            layoutChangeEvent()
        }
    }

    private fun layoutChangeEvent(){
        viewModel.setStateEvent(
            ProductStateEvent.ProductLayoutChangeEvent()
        )
    }
}