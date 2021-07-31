package com.au.a4x4vehiclehirefraser.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.au.a4x4vehiclehirefraser.dto.Service

@Dao
interface ServiceDao {
    @Query("Select * from service where rego = :rego")
    fun getServiceDetailPerRego(rego: String): LiveData<List<Service>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllService(plants: ArrayList<Service>)

    @Delete
    fun delete(service: Service)

    @Query("Delete from Service")
    fun deleteAllServiceEntries()
}