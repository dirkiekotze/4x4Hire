package com.au.a4x4vehiclehirefraser.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.au.a4x4vehiclehirefraser.dto.Hire


@Dao
interface HireDao {
    @Query("Select * from Hire where rego = :rego")
    fun getHireDetailPerRego(rego: String): LiveData<List<Hire>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllHire(hire: ArrayList<Hire>)

    @Delete
    fun delete(hire: Hire)

    @Query("Delete from Hire")
    fun deleteAllHireEntries()
}