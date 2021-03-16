package com.au.a4x4vehiclehirefraser.ui.main

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.*
import com.au.a4x4vehiclehirefraser.examples.DocSnippets
import com.au.a4x4vehiclehirefraser.helper.OneTimeOnly
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.add_vehicle_fragment.*

class MainViewModel : ViewModel() {

    public lateinit var firestore: FirebaseFirestore
    private var _vehicles: MutableLiveData<ArrayList<Vehicle>> =
        MutableLiveData<ArrayList<Vehicle>>()
    private var _service: MutableLiveData<ArrayList<ServiceItem>> =
        MutableLiveData<ArrayList<ServiceItem>>()
    private var _type: MutableLiveData<ArrayList<Type>> = MutableLiveData<ArrayList<Type>>()
    private var storageReferenence = FirebaseStorage.getInstance().getReference()
    private var _user: FirebaseUser? = null
    private var _photos: java.util.ArrayList<Photo> = java.util.ArrayList<Photo>()
    var addServiceId = MutableLiveData<OneTimeOnly<String>>()
    var addServiceItemId = MutableLiveData<OneTimeOnly<String>>()
    var showServiceDetail = MutableLiveData<OneTimeOnly<Service>>()
    var showServiceItems = MutableLiveData<OneTimeOnly<ArrayList<ServiceItem>>>()
    var serviceSaveBtnVisibility = MutableLiveData<OneTimeOnly<Int>>()
    var addServiceItemBtnVisibility = MutableLiveData<OneTimeOnly<Int>>()
    var displayServiceAndItems = MutableLiveData<OneTimeOnly<Boolean>>()

    init {
        //Cloud Firestore Initialization
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToVehicles()
        listenToService()
        listenToType()

        // Run snippets

        // Run snippets
//        val docSnippets = DocSnippets(firestore)
//        docSnippets.runAll()
    }

    private fun listenToVehicles() {
        firestore.collection("vehicle").addSnapshotListener { snapshot, e ->

            //Skip if excepion
            if (e != null) {
                Log.w(TAG, "Listener for Vehicle Failed")
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

    private fun listenToService() {
        firestore.collection("service").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listener for Service failed")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val allServices = ArrayList<ServiceItem>()
                val documents = snapshot.documents
                documents.forEach {
                    val service = it.toObject(ServiceItem::class.java)
                    if (service != null) {
                        allServices.add(service)
                    }
                }
                _service.value = allServices
            }
        }
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

    fun saveVehicle(vehicle: Vehicle, photos: java.util.ArrayList<Photo>, userId: String?) {

        val document: DocumentReference = firestore.collection("vehicle").document(vehicle.rego)
        val set = document.set(vehicle)
        set.addOnSuccessListener {
            Log.d("Firebase", "Vehicle Saved")
            if (photos != null && photos.size > 0) {
                savePhotos(vehicle, photos, userId!!)
                _photos = java.util.ArrayList<Photo>()
            }

        }
        set.addOnFailureListener {
            Log.d("firestore", "Vehicle not saved")
        }

    }

    private fun savePhotos(vehicle: Vehicle, photos: ArrayList<Photo>, userId: String) {

        //Create document as child of vehicle doc
        val collection = firestore.collection("vehicle")
            .document(vehicle.rego)
            .collection("photos")
        photos.forEach { photo ->
            val task = collection.add(photo)
            task.addOnSuccessListener {
                photo.id = it.id
                uploadPhotos(vehicle, photo, userId)
            }
        }
    }

    private fun uploadPhotos(vehicle: Vehicle, photo: Photo, userId: String) {

        var uri = Uri.parse(photo.localUri)
        val imageRef = storageReferenence.child("images/" + userId + "/" + uri.lastPathSegment)
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val downloadUrl = imageRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                photo.remoteUri = it.toString()
                // update our Cloud Firestore with the public image URI.
                updatePhotoDatabase(vehicle, photo)

            }

        }
        uploadTask.addOnFailureListener {
            Log.e(TAG, it.message)
        }

    }

    private fun updatePhotoDatabase(vehicle: Vehicle, photo: Photo) {
        firestore.collection("vehicle")
            .document(vehicle.rego)
            .collection("photos")
            .document(photo.id)
            .set(photo)
    }

    fun saveService(service: Service) {
        firestore.collection("service")
            .add(service)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Service Saved")
                addServiceId.value = OneTimeOnly(documentReference.id)
                //Update id in Service to documentId
                service.id = documentReference.id
                updateService(service, documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.d("Firebase", "Service not saved")
            }
    }

    private fun updateService(service: Service, id: String) {
        firestore.collection("service").document(id)
            .set(service)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Service Updated")
            }
            .addOnFailureListener { e ->
                Log.d("Firebase", "Service Updated")
            }
    }

    fun saveServiceItem(serviceItem: ServiceItem) {
        firestore.collection("serviceItem")
            .add(serviceItem)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Service Item Saved")
                addServiceItemId.value = OneTimeOnly(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.d("Firebase", "Service not saved")
            }
    }


//    fun deleteService(service: ServiceItem) {
//
//        val document: DocumentReference
//        document = firestore.collection("service").document(service.id)
//        document.delete()
//            .addOnSuccessListener {
//                Log.d(TAG, "Service entry Deleted")
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Unable to Delete SErvice Item", e)
//            }
//
//    }

    fun deleteServicePerId(id: String) {

        val toDelete = firestore.collection("service").whereEqualTo("description", "testVehicle")
            .get()
            .addOnSuccessListener {
                it.documents.forEach {
                    //it.
                }
            }
    }

    internal fun getAllVehicles(): ArrayList<Vehicle> {

        var vehicleArrayList = ArrayList<Vehicle>()
        firestore.collection("vehicle")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    vehicleArrayList.add(
                        Vehicle(
                            document.get("model").toString(),
                            document.get("rego").toString(),
                            document.get("description").toString()
                        )
                    )
                }
            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Vehicle in Firestore: $vehicle.rego")
            }

        return vehicleArrayList
    }

    fun getService(serviceId: String) {

        var service = Service()
        firestore.collection("service").document(serviceId)
            .get()
            .addOnSuccessListener { document ->
                Log.d("firestore", "Select Service from Firestore for ServiceId: $serviceId")
                with(service) {
                    id = document.get("id").toString()
                    description = document.get("description").toString()
                    date = document.get("date").toString()
                    kms = document.get("kms").toString().toDouble()
                    price = document.get("price").toString().toDouble()
                }

                //Callback to AddService
                showServiceDetail.value = OneTimeOnly(service)
            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to select Service from Firestore ServiceId: $serviceId")
            }

    }

    fun getServiceItem(serviceId: String) {

        var serviceItemArrayList = ArrayList<ServiceItem>()
        firestore.collection("serviceItem")
            .whereEqualTo("serviceId", serviceId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    serviceItemArrayList.add(
                        ServiceItem(
                            price = document.get("price").toString().toDouble(),
                            description = document.get("description").toString()
                        )
                    )
                }

                showServiceItems.value = OneTimeOnly(serviceItemArrayList)

            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Service in Firestore:")
            }
    }

    fun getServiceSaveBtnVisibility(serviceId: String) {
        if ((serviceId != "null") && (serviceId != "")) {
            serviceSaveBtnVisibility.value = OneTimeOnly(View.GONE)
            displayServiceAndItems.value = OneTimeOnly(true)
        } else {
            serviceSaveBtnVisibility.value = OneTimeOnly(View.VISIBLE)
        }
    }

    fun getAddServiceItemBtnVisibility(serviceItemId: String){
        if ((serviceItemId != "null") && (serviceItemId != "")) {
            addServiceItemBtnVisibility.value = OneTimeOnly(View.VISIBLE)
        } else {
            addServiceItemBtnVisibility.value = OneTimeOnly(View.GONE)
        }
    }

//    internal fun getServiceIdFromFirestore(service: ServiceItem) {
//
//        firestore.collection("service")
//            .whereEqualTo("description", service.description)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    service.id = document.get("id").toString()
//                }
//                Log.d("firestore", "Found Service in Firestore: $service.description")
//                saveService(service)
//
//            }
//            .addOnFailureListener {
//                Log.d("firestore", "Unable to find Service in Firestore: $service.description")
//                saveService(service)
//            }
//    }

    internal var vehicle: MutableLiveData<ArrayList<Vehicle>>
        get() {
            return _vehicles
        }
        set(value) {
            _vehicles = value
        }

    internal var service: MutableLiveData<ArrayList<ServiceItem>>
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