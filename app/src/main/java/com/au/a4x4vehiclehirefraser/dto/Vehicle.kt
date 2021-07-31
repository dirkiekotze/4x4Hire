package com.au.a4x4vehiclehirefraser.dto

data class Vehicle (var Model:String = "", var Rego:String = "", var Description:String = "", var YearModel:Int = 0, var Kms:Int = 0, var Color:String = ""){

    override fun toString(): String {
        return "$Description $Rego $Color  "
    }
}