package com.au.a4x4vehiclehirefraser.dto

data class Type (var id:String = "", var value:String = ""){

    override fun toString(): String {
        return "$value"
    }
}