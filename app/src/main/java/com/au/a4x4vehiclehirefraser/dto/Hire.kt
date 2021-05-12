package com.au.a4x4vehiclehirefraser.dto

data class Hire(var id: String = "",var rego:String = "",
                var startDate: String = "",var endDate: String = "",var days:Int = 0,
                var name: String = "",var email: String = "",var note: String = "",
                var price: Double = 0.0,
                var milliseconds: Long = 0,
                var kms:Int = 0) {

    override fun toString(): String {
        return "$name $startDate $endDate $email $note"
    }

}