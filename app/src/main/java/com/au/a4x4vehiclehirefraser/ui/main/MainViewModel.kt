package com.au.a4x4vehiclehirefraser.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.dto.Type
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query

class MainViewModel : ViewModel() {

    private lateinit var firestore: FirebaseFirestore
    private var _vehicles: MutableLiveData<ArrayList<Vehicle>> =
        MutableLiveData<ArrayList<Vehicle>>()
    private var _service: MutableLiveData<ArrayList<Service>> =
        MutableLiveData<ArrayList<Service>>()
    private var _type: MutableLiveData<ArrayList<Type>> = MutableLiveData<ArrayList<Type>>()

    init {
        //Cloud Firestore Initialization
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToVehicles()
        listenToService()
        listenToType()
    }

    private fun listenToType() {
        firestore.collection("type")
            .orderBy("id", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listener for Type failed")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val allTypes = ArrayList<Type>()
                    val documents = snapshot.documents
                    documents.forEach {
                        val type = it.toObject(Type::class.java)
                        if (type != null) {
                            allTypes.add(type)
                        }
                    }
                    Log.w(TAG, "Listener for Type found values")
                    _type.value = allTypes
                }
            }
    }

    private fun listenToService() {
        firestore.collection("service").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listener for Service failed")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val allServices = ArrayList<Service>()
                val documents = snapshot.documents
                documents.forEach {
                    val service = it.toObject(Service::class.java)
                    if (service != null) {
                        allServices.add(service)
                    }
                }
                _service.value = allServices
            }
        }
    }


    /**
     * This will listen to Firestore for Vehicle updates
     */
    private fun listenToVehicles() {
        firestore.collection("vehicle").addSnapshotListener { snapshot, e ->

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

    fun save(vehicle: Vehicle) {

        val document: DocumentReference

        if (vehicle.id == "") {
            document = firestore.collection("vehicle").document()
        } else {
            document = firestore.collection("vehicle").document(vehicle.id)
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

    fun saveService(service: Service) {

        val document: DocumentReference

        if (service.id == "") {
            document = firestore.collection("service").document()
        } else {
            document = firestore.collection("service").document(service.id)
        }

        service.id = document.id

        val set = document.set(service)
        set.addOnCanceledListener {
            Log.d("Firebase", "Service Saved")
        }
        set.addOnFailureListener {
            Log.d("Firebase", "Service not saved")
        }
    }

    internal fun getVehicleIdFromFirestore(vehicle: Vehicle) {

        firestore.collection("vehicle")
            .whereEqualTo("rego", vehicle.rego)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    vehicle.id = document.get("id").toString()
                }
                Log.d("firestore", "Found Vehicle in Firestore: $vehicle.rego")
                save(vehicle)

            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Vehicle in Firestore: $vehicle.rego")
                save(vehicle)
            }
    }

    internal fun getServiceIdFromFirestore(service: Service) {

        firestore.collection("service")
            .whereEqualTo("description", service.description)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    service.id = document.get("id").toString()
                }
                Log.d("firestore", "Found Service in Firestore: $service.description")
                saveService(service)

            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Service in Firestore: $service.description")
                saveService(service)
            }
    }

    fun fetchServiceItem(vehicleType: String, description: String):String {

        var retValue:String = ""
        firestore.collection("Service")
            .whereEqualTo("vehicleType", vehicleType)
            .whereEqualTo("description", description)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    retValue = document.get("description").toString()
                }
            }
            return retValue
    }

    internal var vehicle: MutableLiveData<ArrayList<Vehicle>>
        get() {
            return _vehicles
        }
        set(value) {
            _vehicles = value
        }

    internal var service: MutableLiveData<ArrayList<Service>>
        get() {
            return _service
        }
        set(value) {
            _service = value
        }

    internal var type: MutableLiveData<ArrayList<Type>>
        get() {
            return _type
        }
        set(value) {
            _type = value
        }

}