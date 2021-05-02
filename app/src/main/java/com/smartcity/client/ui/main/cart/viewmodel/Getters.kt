package com.smartcity.client.ui.main.cart.viewmodel

import com.smartcity.client.models.Address
import com.smartcity.client.models.Bill
import com.smartcity.client.models.OrderType
import com.smartcity.client.models.product.Cart
import com.smartcity.provider.models.Policy

fun CartViewModel.getCartList(): Cart? {
    getCurrentViewStateOrNew().let {
        return it.cartFields.cartList
    }
}

fun CartViewModel.getStorePolicy(): Policy? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.storePolicy
    }
}

fun CartViewModel.getSelectedCartProduct(): Cart? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.selectedCartProduct
    }
}

fun CartViewModel.getTotalBill(): Bill? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.total
    }
}
fun CartViewModel.getOrderType(): OrderType? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderType
    }
}

fun CartViewModel.getAddressList(): List<Address> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.addressList
    }
}

fun CartViewModel.getDeliveryAddress(): Address? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.deliveryAddress
    }
}
