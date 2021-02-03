package com.au.a4x4vehiclehirefraser.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainViewModel : ViewModel() {

    private lateinit var firestore: FirebaseFirestore

    init {
        //Cloud Firestore Initialization
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun save(vehicle: Vehicle,vehicleIdInFirestore:String) {

        val document: DocumentReference

        //You dont want to save vehicle twice
        //You want to have a unique generated id as well as Rego which is unique any way
        //So you need to select vehicle per Rego and then use the document value

        if(vehicleIdInFirestore == ""){
            document = firestore.collection("vehicles").document()
        }else{
            document = firestore.collection("vehicles").document(vehicleIdInFirestore)
        }

        vehicle.id = document.id

        val set = document.set(vehicle)
        set.addOnCanceledListener {
            Log.d("Firebase", "Vehicle Saved")
        }
        set.addOnFailureListener {
            Log.d("firestore", "Vehicle not saved")
        }

    }

    private fun doMe(document: DocumentReference) {

    }

    internal fun getVehicleIdFromFirestore(vehicle:Vehicle) {

        var regoFound: String = ""
        firestore.collection("vehicles")
            .whereEqualTo("rego", vehicle.rego)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    regoFound = document.get("id").toString()
                }
                Log.d("firestore", "Found Vehicle in Firestore: $vehicle.rego")
                save(vehicle,regoFound)

            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Vehicle in Firestore: $vehicle.rego")
                save(vehicle,"")
            }



    }

}