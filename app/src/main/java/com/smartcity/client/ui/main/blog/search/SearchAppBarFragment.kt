package com.smartcity.client.ui.main.blog.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.R
import com.smartcity.client.ui.main.blog.state.ProductStateEvent
import com.smartcity.client.ui.main.blog.viewmodel.*
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.SuccessHandling.Companion.DONE_Search_Event
import kotlinx.android.synthetic.main.fragment_search_app_bar.*


class SearchAppBarFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): Fragment() {

    val viewModel: ProductViewModel by viewModels{
        viewModelFactory
    }

    private lateinit var productSearchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_app_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchView()
        onBackClicked()
        setGridListViewBehavior()
        initGridListViewBehavior(viewModel.getGridOrListViewSearch())
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
            viewModel.setGridOrListViewSearch(true)
            layoutChangeEvent()
        }

        grid_view_button.setOnClickListener {
            grid_view_button.visibility=View.GONE
            list_view_button.visibility =View.VISIBLE
            viewModel.setGridOrListViewSearch(false)
            layoutChangeEvent()
        }
    }

    private fun layoutChangeEvent(){
        viewModel.setStateEvent(
            ProductStateEvent.ProductLayoutChangeEvent()
        )
    }

    private fun onBackClicked() {
        back_button.setOnClickListener {
            viewModel.setStateEvent(
                ProductStateEvent.BackClickedEvent(
                    SuccessHandling.DONE_Back_Clicked_Store_Event
                )
            )
        }
    }

    private fun initSearchView(){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            productSearchView = product_search_view
            productSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            productSearchView.maxWidth = Integer.MAX_VALUE
            productSearchView.setIconifiedByDefault(false)
            productSearchView.isSubmitButtonEnabled = true
            productSearchView.requestFocus()
            productSearchView.queryHint="Search"

        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = productSearchView.findViewById(R.id.search_src_text) as EditText

        val backgroundView = productSearchView.findViewById(R.id.search_plate) as View
        backgroundView.background = null

        val backgroundViewSubmit = productSearchView.findViewById(R.id.submit_area) as View
        backgroundViewSubmit.background = null

        searchPlate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                viewModel.setQuerySearch(searchQuery)
                searchEvent()
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = productSearchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            viewModel.setQuerySearch(searchQuery)
            searchEvent()
        }
    }

    private fun searchEvent(){
        viewModel.setStateEvent(
            ProductStateEvent.SearchEvent(DONE_Search_Event)
        )
    }
}