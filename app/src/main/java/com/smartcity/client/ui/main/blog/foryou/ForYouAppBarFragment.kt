package com.smartcity.client.ui.main.blog.foryou

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.R
import com.smartcity.client.ui.main.blog.viewmodel.ProductViewModel


class ForYouAppBarFragment
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
        return inflater.inflate(R.layout.fragment_for_you_app_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}