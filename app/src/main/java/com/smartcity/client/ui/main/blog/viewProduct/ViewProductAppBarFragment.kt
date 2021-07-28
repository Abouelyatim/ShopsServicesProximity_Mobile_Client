package com.smartcity.client.ui.main.blog.viewProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.R
import com.smartcity.client.ui.main.blog.state.ProductStateEvent
import com.smartcity.client.ui.main.blog.viewmodel.ProductViewModel
import com.smartcity.client.util.SuccessHandling.Companion.DONE_Back_Clicked_View_product_Event
import kotlinx.android.synthetic.main.fragment_view_product_app_bar.*


class ViewProductAppBarFragment
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
        return inflater.inflate(R.layout.fragment_view_product_app_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackClicked()
    }

    private fun onBackClicked() {
        back_button.setOnClickListener {
            viewModel.setStateEvent(
                ProductStateEvent.BackClickedEvent(
                    DONE_Back_Clicked_View_product_Event
                )
            )
        }
    }
}