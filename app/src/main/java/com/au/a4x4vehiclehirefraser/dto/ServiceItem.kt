package com.au.a4x4vehiclehirefraser.dto

data class ServiceItem (var description:String = "", var price:Double = 0.0, var quantity:Int = 0){

    override fun toString(): String {
        return "$description "
    }
}