package com.au.a4x4vehiclehirefraser.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainViewModel : ViewModel() {

    private lateinit var firestore: FirebaseFirestore
    private var _vehicles: MutableLiveData<ArrayList<Vehicle>> = MutableLiveData<ArrayList<Vehicle>>()

    init {
        //Cloud Firestore Initialization
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToVehicles()
    }



    /**
     * This will listen to Firestore for Vehicle updates
     */
    private fun listenToVehicles() {
        firestore.collection("vehicles").addSnapshotListener { snapshot, e ->

            //Skip if excepion
            if (e != null) {
                Log.w(TAG, "Listener Failed")
                return@addSnapshotListener
            }

            // if we are here, we did not encounter an exception
            if (snapshot != null) {
                val allVehicles = ArrayList<Vehicle>()
                val documents = snapshot.documents
                documents.forEach {
                    val vehicle = it.toObject(Vehicle::class.java)
                    if (vehicle != null) {
                        allVehicles.add(vehicle!!)
                    }
                }
                _vehicles.value = allVehicles
            }


        }
    }

    fun save(vehicle: Vehicle, vehicleIdInFirestore: String) {

        val document: DocumentReference

        if (vehicleIdInFirestore == "") {
            document = firestore.collection("vehicles").document()
        } else {
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


    internal fun getVehicleIdFromFirestore(vehicle: Vehicle) {

        var vehicleIdInFirestore: String = ""
        firestore.collection("vehicles")
            .whereEqualTo("rego", vehicle.rego)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    vehicleIdInFirestore = document.get("id").toString()
                }
                Log.d("firestore", "Found Vehicle in Firestore: $vehicle.rego")
                save(vehicle, vehicleIdInFirestore)

            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Vehicle in Firestore: $vehicle.rego")
                save(vehicle, "")
            }


    }

    internal var vehicle: MutableLiveData<ArrayList<Vehicle>>
        get() {
            return _vehicles
        }
        set(value) {
            _vehicles = value
        }

}