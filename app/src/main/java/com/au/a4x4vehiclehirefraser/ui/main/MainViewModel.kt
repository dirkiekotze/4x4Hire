package com.au.a4x4vehiclehirefraser.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainViewModel : ViewModel() {

    private lateinit var firestore: FirebaseFirestore

    init {
        //Cloud Firestore Initialization
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun save(vehicle: Vehicle) {

        //You dont want to save vehicle twice
        //You want to have a unique generated id as well as Rego which is unique any way
        //So you need to select vehicle per Rego and then use the document value

        val document = firestore.collection("vehicles").document()
        vehicle.id = document.id

        val set = document.set(vehicle)
        set.addOnCanceledListener {
            Log.d("Firebase","Vehicle Saved")
        }
        set.addOnFailureListener {
            Log.d("firestore","Vehicle not saved")
        }

    }

}