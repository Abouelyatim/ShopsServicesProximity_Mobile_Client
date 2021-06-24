package com.smartcity.client.ui.main.flash_notification.viewmodel

import com.smartcity.client.models.FlashDeal

fun FlashViewModel.getFlashDealsList(): List<FlashDeal> {
    getCurrentViewStateOrNew().let {
        return it.flashFields.flashDealsList
    }
}