package com.au.a4x4vehiclehirefraser.dto

data class ServiceItem (var id:String = "",var serviceId:String = "",var description:String = "", var price:Double = 0.0, var quantity:String = ""){

    override fun toString(): String {
        return "$description $price"
    }
}