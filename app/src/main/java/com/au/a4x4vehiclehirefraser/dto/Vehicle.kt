package com.au.a4x4vehiclehirefraser.dto

data class Vehicle (var model:String = "",var rego:String = "",var description:String = "",var yearModel:Int = 0,var kms:Int = 0,var color:String = ""){

    override fun toString(): String {
        return "$yearModel $description "
    }
}