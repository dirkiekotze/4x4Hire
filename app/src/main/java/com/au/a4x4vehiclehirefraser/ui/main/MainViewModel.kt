package com.au.a4x4vehiclehirefraser.ui.main

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.*
import com.au.a4x4vehiclehirefraser.helper.Constants.ADDED_SERVICE_ITEM
import com.au.a4x4vehiclehirefraser.helper.Constants.DELETED_HIRE
import com.au.a4x4vehiclehirefraser.helper.Constants.DELETED_SERVICE
import com.au.a4x4vehiclehirefraser.helper.Constants.DELETED_SERVICE_ITEM
import com.au.a4x4vehiclehirefraser.helper.Constants.DELETED_VEHICLE
import com.au.a4x4vehiclehirefraser.helper.Constants.NOTHING_TO_DISPLAY
import com.au.a4x4vehiclehirefraser.helper.OneTimeOnly
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.add_vehicle_fragment.*
import kotlinx.android.synthetic.main.main_fragment.*

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
    var hireId = MutableLiveData<OneTimeOnly<String>>()
    var addServiceItemId = MutableLiveData<OneTimeOnly<String>>()
    var showVehiclePerRego = MutableLiveData<OneTimeOnly<Vehicle>>()
    var showAllVehicles = MutableLiveData<OneTimeOnly<ArrayList<Vehicle>>>()
    var showServiceDetail = MutableLiveData<OneTimeOnly<Service>>()
    var showServiceDetailPerRego = MutableLiveData<OneTimeOnly<ArrayList<Service>>>()
    var hideServiceDetailPerRego = MutableLiveData<OneTimeOnly<String>>()
    var hideAllWithMessage = MutableLiveData<OneTimeOnly<String>>()
    var showHireDetail = MutableLiveData<OneTimeOnly<ArrayList<Hire>>>()
    var showHireDetailSingle = MutableLiveData<OneTimeOnly<Hire>>()
    var showServiceItems = MutableLiveData<OneTimeOnly<ArrayList<ServiceItem>>>()
    var showServiceItem = MutableLiveData<OneTimeOnly<ServiceItem>>()
    var serviceSaveBtnVisibility = MutableLiveData<OneTimeOnly<Int>>()
    var serviceSaveBtnText = MutableLiveData<OneTimeOnly<Int>>()
    var addServiceItemBtnVisibility = MutableLiveData<OneTimeOnly<Int>>()
    var displayServiceAndItems = MutableLiveData<OneTimeOnly<Boolean>>()
    var validToAddService = MutableLiveData<OneTimeOnly<Boolean>>()
    var validToAddServiceItem = MutableLiveData<OneTimeOnly<Boolean>>()
    var validToAddHire = MutableLiveData<OneTimeOnly<Boolean>>()
    var displayToast = MutableLiveData<OneTimeOnly<String>>()
    var deletedServiceItem = MutableLiveData<OneTimeOnly<String>>()


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

    fun saveHire(hire: Hire) {
        //Update if you have Id already
        if (!hire.id.isNullOrEmpty()) {
            updateHire(hire, hire.id, true)
        } else {
            firestore.collection("hire")
                .add(hire)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firebase", "Hire Saved")
                    hire.id = documentReference.id
                    updateHire(hire, documentReference.id, true)
                }
                .addOnFailureListener { e ->
                    Log.d("Firebase", "Service not saved")
                }
        }

    }

    private fun updateHire(hire: Hire, id: String, showToast: Boolean) {
        firestore.collection("hire").document(id)
            .set(hire)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Hire Updated")
                if (showToast) {
                    hireId.value = OneTimeOnly(id)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Firebase", "Hire Updated")
            }
    }

    fun saveService(service: Service) {
        //Update if you have Id already
        if (!service.id.isNullOrEmpty()) {
            updateService(service, service.id, true)
        } else {
            firestore.collection("service")
                .add(service)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firebase", "Service Saved")
                    addServiceId.value = OneTimeOnly(documentReference.id)
                    service.id = documentReference.id
                    updateService(service, documentReference.id, false)
                }
                .addOnFailureListener { e ->
                    Log.d("Firebase", "Service not saved")
                }
        }

    }

    private fun updateService(service: Service, id: String, showToast: Boolean) {
        firestore.collection("service").document(id)
            .set(service)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Service Updated")
                if (showToast) {
                    addServiceId.value = OneTimeOnly(id)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Firebase", "Service Updated")
            }
    }

    fun saveServiceItem(serviceItem: ServiceItem) {
        if (!serviceItem.id.isNullOrEmpty()) {
            updateServiceItem(serviceItem, serviceItem.id, true)
        } else {
            firestore.collection("serviceItem")
                .add(serviceItem)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firebase", "ServiceItem Saved")
                    addServiceItemId.value = OneTimeOnly(documentReference.id)
                    serviceItem.id = documentReference.id
                    updateServiceItem(serviceItem, documentReference.id, true)
                }
                .addOnFailureListener { e ->
                    Log.d("Firebase", "ServiceItem not saved")
                }
        }
    }

    private fun updateServiceItem(serviceItem: ServiceItem, id: String, showToastFlag: Boolean) {
        firestore.collection("serviceItem").document(id)
            .set(serviceItem)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Service Updated")
                if (showToastFlag) {
                    displayToast.value = OneTimeOnly(ADDED_SERVICE_ITEM)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Firebase", "Service Updated")
            }
    }


    fun deleteServicePerId(id: String) {

        firestore.collection("service").whereEqualTo("id", id).get()
            .addOnSuccessListener {
                var batch = firestore.batch();
                it.forEach {
                    //Todo: Test to see if this works
                    it.reference.delete()
                }

                batch.commit();
                Log.w("firestore", "Deleted $id")
                displayToast.value = OneTimeOnly(DELETED_SERVICE)

            }
            .addOnFailureListener {
                Log.w("firestore", "Unable to delete $id")
            }


    }

    fun deleteHire(hireId: String?) {
        firestore.collection("hire").whereEqualTo("id", hireId).get()
            .addOnSuccessListener {
                var batch = firestore.batch();
                it.forEach {
                    it.reference.delete()
                }

                batch.commit();
                Log.w("firestore", "Deleted $hireId")
                displayToast.value = OneTimeOnly(DELETED_HIRE)

            }
            .addOnFailureListener {
                Log.w("firestore", "Unable to delete $hireId")
            }
    }

    fun validateService(
        serviceDate: Int,
        serviceDescription: Int,
        servicePrice: Int,
        serviceKms: Int
    ) {
        if ((serviceDate > 0) && (serviceDescription > 0) && (servicePrice > 0) && (serviceKms > 0)) {
            validToAddService.value = OneTimeOnly(true)
        } else {
            validToAddService.value = OneTimeOnly(false)
        }
    }

    fun validateHire(
        hireStartDate: Int,
        hireEndDate: Int,
        hireDays:Int,
        hireName: Int,
        hireEmail: Int,
        hireNote: Int,
        hireKms:Int
    ) {
        if ((hireStartDate > 0) && (hireEndDate > 0) && (hireDays > 0) && (hireName > 0) && (hireEmail > 0) && (hireNote > 0) &&(hireKms > 0)) {
            validToAddHire.value = OneTimeOnly(true)
        } else {
            validToAddHire.value = OneTimeOnly(false)
        }
    }

    fun validateServiceItem(serviceDescription: Int, servicePrice: Int, serviceQuantity: Int) {
        if ((serviceDescription > 0) && (servicePrice > 0) && (serviceQuantity > 0)) {
            validToAddServiceItem.value = OneTimeOnly(true)
        } else {
            validToAddServiceItem.value = OneTimeOnly(false)
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
                showAllVehicles.value = OneTimeOnly(vehicleArrayList)
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
                    dateMilliseconds = document.get("dateMilliseconds").toString().toLong()
                }

                //Callback to AddService
                showServiceDetail.value = OneTimeOnly(service)
            }
            .addOnFailureListener {
                Log.d(
                    "firestore",
                    "Unable to select Service from Firestore ServiceId: $serviceId"
                )
            }

    }

    fun getServicePerRego(rego: String) {

        var serviceArrayList = java.util.ArrayList<Service>()
        firestore.collection("service")
            .whereEqualTo("rego", rego)
            .orderBy("dateMilliseconds", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->


                for (document in documents) {
                    serviceArrayList.add(
                        Service(
                            id = document.get("id").toString(),
                            date = document.get("date").toString(),
                            description = document.get("description").toString(),
                            kms = document.get("kms").toString().toDouble(),
                            rego = document.get("rego").toString(),
                            price = document.get("price").toString().toDouble(),
                            dateMilliseconds = document.get("dateMilliseconds").toString().toLong()

                        )
                    )
                }

                if (serviceArrayList.size == 0) {
                    hideAllWithMessage.value = OneTimeOnly(NOTHING_TO_DISPLAY)
                } else {
                    showServiceDetailPerRego.value = OneTimeOnly(serviceArrayList)
                }


            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Service in Firestore:Message $it")
                hideAllWithMessage.value = OneTimeOnly(it.message!!)
            }


    }

    fun getHirePerRego(rego: String) {

        var hireArrayList = java.util.ArrayList<Hire>()
        firestore.collection("hire")
            .whereEqualTo("rego", rego)
            .orderBy("milliseconds", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    hireArrayList.add(
                        Hire(
                            id = document.get("id").toString(),
                            rego = document.get("rego").toString(),
                            startDate = document.get("startDate").toString(),
                            endDate = document.get("endDate").toString(),
                            days = document.get("days").toString().toInt(),
                            name = document.get("name").toString(),
                            email = document.get("email").toString(),
                            note = document.get("note").toString(),
                            price = document.get("price").toString().toDouble(),
                            milliseconds = document.get("milliseconds").toString().toLong()
                        )
                    )
                }

                if (hireArrayList.size == 0) {
                    hideAllWithMessage.value = OneTimeOnly(NOTHING_TO_DISPLAY)
                } else {
                    showHireDetail.value = OneTimeOnly(hireArrayList)
                }


            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Hire in Firestore:Message $it")
                hideAllWithMessage.value = OneTimeOnly(it.message!!)
            }


    }


    fun getHirePerId(id: String) {

        var hire = Hire()
        firestore.collection("hire")
            .whereEqualTo("id", id)
            .orderBy("milliseconds", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    hire =
                        Hire(
                            id = document.get("id").toString(),
                            rego = document.get("rego").toString(),
                            startDate = document.get("startDate").toString(),
                            endDate = document.get("endDate").toString(),
                            days = document.get("days").toString().toInt(),
                            name = document.get("name").toString(),
                            email = document.get("email").toString(),
                            note = document.get("note").toString(),
                            price = document.get("price").toString().toDouble(),
                            milliseconds = document.get("milliseconds").toString().toLong()
                        )

                }

                if (hire != null) {
                    showHireDetailSingle.value = OneTimeOnly(hire!!)
                }


            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Hire in Firestore:Message $it")
                hideAllWithMessage.value = OneTimeOnly(it.message!!)
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
                            quantity = document.get("quantity").toString(),
                            description = document.get("description").toString(),
                            id = document.get("id").toString(),
                            serviceId = document.get("serviceId").toString()
                        )
                    )
                }

                showServiceItems.value = OneTimeOnly(serviceItemArrayList)

            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Service in Firestore:")
            }
    }

    fun getServiceItemPerId(id: String) {

        var serviceItem = ServiceItem()
        serviceItem = ServiceItem()
        firestore.collection("serviceItem")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    serviceItem = ServiceItem(
                        price = document.get("price").toString().toDouble(),
                        quantity = document.get("quantity").toString(),
                        description = document.get("description").toString(),
                        id = document.get("id").toString(),
                        serviceId = document.get("serviceId").toString()
                    )

                }

                showServiceItem.value = OneTimeOnly(serviceItem)

            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Service ITem in Firestore:")
            }
    }

    fun getServiceSaveBtnVisibility(serviceId: String) {
        if ((serviceId != "null") && (serviceId != "")) {
            serviceSaveBtnVisibility.value = OneTimeOnly(View.VISIBLE)
            displayServiceAndItems.value = OneTimeOnly(true)
            serviceSaveBtnText.value = OneTimeOnly(R.string.edit_service)
        } else {
            serviceSaveBtnVisibility.value = OneTimeOnly(View.VISIBLE)
        }
    }

    fun getAddServiceItemBtnVisibility(serviceItemId: String) {
        if ((serviceItemId != "null") && (serviceItemId != "")) {
            addServiceItemBtnVisibility.value = OneTimeOnly(View.VISIBLE)
        } else {
            addServiceItemBtnVisibility.value = OneTimeOnly(View.GONE)
        }
    }

    fun getVehiclePerRego(regoVal: String) {

        var vehicle = Vehicle()
        firestore.collection("vehicle")
            .whereEqualTo("rego", regoVal)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    with(vehicle) {
                        model = document.get("model").toString()
                        rego = document.get("rego").toString()
                        description = document.get("description").toString()
                        yearModel = document.get("yearModel").toString().toInt()
                        kms = document.get("kms").toString().toInt()
                        color = document.get("color").toString()

                    }

                }


                if (vehicle != null) {
                    showVehiclePerRego.value = OneTimeOnly(vehicle)
                }


            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Service in Firestore:")
            }


    }

    fun deleteVehicle(rego: String?) {

        firestore.collection("vehicle").whereEqualTo("rego", rego).get()
            .addOnSuccessListener {
                var batch = firestore.batch();
                it.forEach {
                    //Todo: Test to see if this works
                    it.reference.delete()
                }

                batch.commit();
                Log.w("firestore", "Deleted $rego")
                displayToast.value = OneTimeOnly(DELETED_VEHICLE)
            }
            .addOnFailureListener {
                Log.w("firestore", "Unable to delete $rego")
            }
    }

    fun deleteServiceItem(id: String?) {
        firestore.collection("serviceItem").whereEqualTo("id", id).get()
            .addOnSuccessListener {
                var batch = firestore.batch();
                it.forEach {
                    //Todo: Test to see if this works
                    it.reference.delete()
                }

                batch.commit();
                Log.w("firestore", "Deleted ServiceItem $id")
                deletedServiceItem.value = OneTimeOnly(DELETED_SERVICE_ITEM)
            }
            .addOnFailureListener {
                Log.w("firestore", "Unable to delete ServiceItem $id")
            }
    }




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