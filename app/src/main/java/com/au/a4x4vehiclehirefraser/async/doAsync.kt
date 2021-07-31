package com.au.a4x4vehiclehirefraser.async

import android.os.AsyncTask
import android.os.AsyncTask.execute

class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    init {
        execute()
    }


    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}