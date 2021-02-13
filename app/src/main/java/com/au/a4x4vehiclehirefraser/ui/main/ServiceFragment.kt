package com.au.a4x4vehiclehirefraser.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Service
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.vehicle_fragment.*

class ServiceFragment : Fragment() {

    companion object {
        fun newInstance() = ServiceFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var firestore: FirebaseFirestore

    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.service_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity.let {
            viewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }
        //vehicleModelSpinner.setAdapter(ArrayAdapter.createFromResource(context!!, R.array.vehicle_model,android.R.layout.simple_spinner_item))

        saveRepairBtn.setOnClickListener {
            saveService()
        }

        cmdReturnFromRepairToMain.setOnClickListener {
            (activity as MainActivity).showMainFragment()
        }
    }

    private fun saveService() {

        val document: DocumentReference
        val service = Service()

//        document = firestore.collection("service").document()
//        with(service){
//            id = document.id
//            rego = vehicleRego.text.toString()
//            description = serviceDescripion.text.toString()
//            kms = vehicleKms.text.toString().toInt()
//            model = vehicleModelSpinner.selectedItem.toString()
//            yearModel = vehicleYearModel.text.toString().toInt()
//        }
//
//
//
//        val set = document.set(vehicle)
//        set.addOnSuccessListener {
//            Log.d("Firebase", "Vehicle Saved")
//            (activity as MainActivity).showMainFragment()
//        }
//        set.addOnFailureListener {
//            Log.d("firestore", "Vehicle not saved")
//        }
    }

}