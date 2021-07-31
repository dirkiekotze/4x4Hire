package com.au.a4x4vehiclehirefraser.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="service")
data class Service (@PrimaryKey var id:String = "", var rego:String = "",
                    var date:String = "", var price:Double? = 0.0, var description:String = "",
                    var kms:Double? =  0.0, var dateMilliseconds:Long = 0){

    override fun toString(): String {
        return "$date $kms $description"
    }

}