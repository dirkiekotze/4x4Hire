package com.au.a4x4vehiclehirefraser.ui.main

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.au.a4x4vehiclehirefraser.dao.CarsDatabase
import com.au.a4x4vehiclehirefraser.dao.ServiceDao
import com.au.a4x4vehiclehirefraser.dto.Service

class ServiceRoomViewModel(application: Application): AndroidViewModel(application) {

    private val serviceDaoDao: ServiceDao

    init{
        val db = CarsDatabase.getDatabase(application)
        serviceDaoDao = db!!.localServiceDAO()
    }

    fun getServiceDetailPerRego(rego:String): LiveData<List<Service>>?{
        return serviceDaoDao.getServiceDetailPerRego(rego)
    }


    fun populateDbWithServiceData(service: ArrayList<Service>) {
        InsertAsyncTask(serviceDaoDao).execute(service)
    }

    companion object {

        private class InsertAsyncTask(private val serviceDao: ServiceDao) : AsyncTask<ArrayList<Service>, Void, Void>() {

            override fun doInBackground(vararg serviceLst: ArrayList<Service>): Void? {
                serviceDao.insertAllService(serviceLst[0])
                return null
            }
        }

    }


}
