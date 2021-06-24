package com.smartcity.client.ui.main.flash_notification.viewmodel

import com.smartcity.client.models.FlashDeal

fun FlashViewModel.setFlashDealsList(list: List<FlashDeal>) {
    val update = getCurrentViewStateOrNew()
    update.flashFields.flashDealsList=list
    setViewState(update)
}