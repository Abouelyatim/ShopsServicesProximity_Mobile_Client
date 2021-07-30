package com.smartcity.client.repository

import com.smartcity.client.ui.deleted.BaseViewModel

interface BaseRepository<StateEvent,ViewState> {
    fun setCurrentViewModel(viewModel: BaseViewModel<StateEvent, ViewState>)
}