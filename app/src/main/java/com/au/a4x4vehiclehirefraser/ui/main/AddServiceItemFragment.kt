package com.au.a4x4vehiclehirefraser.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.ServiceItem
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.add_service_item_fragment.*

class AddServiceItemFragment : Fragment() {

    companion object {
        fun newInstance() = AddServiceItemFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var firestore: FirebaseFirestore

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_service_item_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity.let {
            viewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }

        clearFields()
        serviceVehicleTypeSpinner.setAdapter(ArrayAdapter.createFromResource(context!!,R.array.vehicle_type,android.R.layout.simple_spinner_item
            )
        )

        serviceSaveBtn.setOnClickListener {
            saveService()
        }

        serviceBackBtn.setOnClickListener {
            (activity as MainActivity).showMainFragment()
        }
    }

    private fun clearFields() {
        serviceDescription.setText("")
        servicePrice.setText("")
        serviceQuantity.setText("")
    }

    private fun saveService() {

        val document: DocumentReference
        val service = ServiceItem()

        document = firestore.collection("service").document()
        with(service) {
            id = document.id
            description = serviceDescription.text.toString()
            price = servicePrice.text.toString().toDouble()
            quantity = serviceQuantity.text.toString().toInt()
            vehicleType = serviceVehicleTypeSpinner.selectedItem.toString()
        }

        val set = document.set(service)
        set.addOnSuccessListener {
            Log.w("Firebase", "Service Saved")
            (activity as MainActivity).showMainFragment()
        }
        set.addOnFailureListener {
            Log.w("firestore", "Service not saved")
        }
    }

}