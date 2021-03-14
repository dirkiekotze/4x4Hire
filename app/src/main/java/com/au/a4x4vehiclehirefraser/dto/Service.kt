package com.au.a4x4vehiclehirefraser.dto

data class Service (var rego:String = "", var date:String = "",var price:Double? = 0.0,  var description:String = "",var kms:Double? =  0.0){

    override fun toString(): String {
        return "$date $kms $description"
    }

}