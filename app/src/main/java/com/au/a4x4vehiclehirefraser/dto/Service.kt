package com.au.a4x4vehiclehirefraser.dto

data class Service (var id:String = "", var vehicleType:String = "", var description:String = "", var price:Double = 0.0,var quantity:Int = 0){

    override fun toString(): String {
        return "$quantity x $vehicleType $description "
    }
}