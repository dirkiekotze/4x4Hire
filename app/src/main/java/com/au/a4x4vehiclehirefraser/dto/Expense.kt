package com.au.a4x4vehiclehirefraser.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="expense")
data class Expense(@PrimaryKey var Id: String = "",
                   var Type:String = "",
                   var Rego:String = "",
                   var Date: String = "",
                   var Note: String = "",
                   var DateMilliseconds:Long = 0,
                   var Price:Double? = 0.0,) {

    override fun toString(): String {
        return "$Price $Date $Rego $Type $Note"
    }

}