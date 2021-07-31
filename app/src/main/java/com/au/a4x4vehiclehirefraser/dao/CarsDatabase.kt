package com.au.a4x4vehiclehirefraser.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.au.a4x4vehiclehirefraser.dto.Hire
import com.au.a4x4vehiclehirefraser.dto.Service

@Database(entities=arrayOf(Service::class, Hire::class), version = 1)
abstract class CarsDatabase:RoomDatabase() {
    abstract fun localServiceDAO() : ServiceDao
    abstract fun localHireDAO() : HireDao

    companion object {

        private var carsDbInstance: com.au.a4x4vehiclehirefraser.dao.CarsDatabase? = null

        fun getDatabase(context: Context): com.au.a4x4vehiclehirefraser.dao.CarsDatabase? {
            if (carsDbInstance == null) {

                synchronized(RoomDatabase::class.java) {
                    if (carsDbInstance == null) {
                        carsDbInstance = Room.databaseBuilder<com.au.a4x4vehiclehirefraser.dao.CarsDatabase>(context.applicationContext,
                            CarsDatabase::class.java, "room_Database")
                            .build()
                    }
                }
            }
            return carsDbInstance
        }
    }

}