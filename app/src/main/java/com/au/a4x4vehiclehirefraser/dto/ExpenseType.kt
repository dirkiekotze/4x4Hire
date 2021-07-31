package com.au.a4x4vehiclehirefraser.dto

data class ExpenseType (var Id:String = "", var Value:String = ""){

    override fun toString(): String {
        return "$Value"
    }
}