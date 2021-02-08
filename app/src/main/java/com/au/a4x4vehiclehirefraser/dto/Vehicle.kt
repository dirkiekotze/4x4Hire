package com.au.a4x4vehiclehirefraser.dto

data class Vehicle (var id:String = "",var rego:String = "",var description:String = "",var yearModel:Int = 0,var kms:Int = 0){

    override fun toString(): String {
        return "$yearModel $description "
    }
}