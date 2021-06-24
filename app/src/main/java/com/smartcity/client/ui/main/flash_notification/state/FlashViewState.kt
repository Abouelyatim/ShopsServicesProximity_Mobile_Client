package com.smartcity.client.ui.main.flash_notification.state

import android.os.Parcelable
import com.smartcity.client.models.FlashDeal
import kotlinx.android.parcel.Parcelize

const val CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY = "com.smartcity.client.ui.main.flash_notification.state.FlashViewState"

@Parcelize
class FlashViewState(
    var flashFields: FlashFields = FlashFields()
    ) : Parcelable {

    @Parcelize
    data class FlashFields(
        var flashDealsList: List<FlashDeal> = listOf()
    ) : Parcelable

}