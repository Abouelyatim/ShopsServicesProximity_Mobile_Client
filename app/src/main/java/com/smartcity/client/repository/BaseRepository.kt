package com.smartcity.client.repository

import com.smartcity.client.ui.BaseViewModel

interface BaseRepository<StateEvent,ViewState> {
    fun setCurrentViewModel(viewModel: BaseViewModel<StateEvent,ViewState>)
}