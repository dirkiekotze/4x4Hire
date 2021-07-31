package com.au.a4x4vehiclehirefraser.ui.main

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.au.a4x4vehiclehirefraser.dao.CarsDatabase
import com.au.a4x4vehiclehirefraser.dao.HireDao
import com.au.a4x4vehiclehirefraser.dao.ServiceDao
import com.au.a4x4vehiclehirefraser.dto.Hire
import com.au.a4x4vehiclehirefraser.dto.Service

class HireRoomViewModel(application: Application): AndroidViewModel(application) {

    private val hireDao: HireDao

    init{
        val db = CarsDatabase.getDatabase(application)
        hireDao = db!!.localHireDAO()
    }

    fun getHireDetailPerRego(rego:String): LiveData<List<Hire>>?{
        return hireDao.getHireDetailPerRego(rego)
    }


    fun populateDbWithHireData(hire: ArrayList<Hire>) {
        InsertAsyncTask(hireDao).execute(hire)
    }

    companion object {

        private class InsertAsyncTask(private val serviceDao: HireDao) : AsyncTask<ArrayList<Hire>, Void, Void>() {

            override fun doInBackground(vararg hireLst: ArrayList<Hire>): Void? {
                serviceDao.insertAllHire(hireLst[0])
                return null
            }
        }

    }


}
