package com.smartcity.provider.models

enum class SelfPickUpOptions {
    //To be able to reserve the articles of your order, the store  requires payment of the total amount corresponds to
    SELF_PICK_UP_TOTAL,

    //To be able to reserve the articles of your order, the store requires the payment of a deposit which corresponds to
    SELF_PICK_UP_PART_PERCENTAGE,
    SELF_PICK_UP_PART_RANGE,

    //This part of the order does not require any payment before getting it from the store
    SELF_PICK_UP
}