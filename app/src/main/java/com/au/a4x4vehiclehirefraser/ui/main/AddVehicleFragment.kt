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
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.add_vehicle_fragment.*

class AddVehicleFragment : Fragment() {

    companion object {
        fun newInstance() = AddVehicleFragment()
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
        return inflater.inflate(R.layout.add_vehicle_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity.let {
            viewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }

        vehicleModelSpinner.setAdapter(ArrayAdapter.createFromResource(context!!, R.array.vehicle_model,android.R.layout.simple_spinner_item))

        saveRepairBtn.setOnClickListener {
            saveVehicle()
        }

        cmdReturnFromRepairToMain.setOnClickListener {
            (activity as MainActivity).showMainFragment()
        }
    }

    private fun saveVehicle() {

        val document: DocumentReference
        val vehicle = Vehicle()

        document = firestore.collection("vehicle").document()
        with(vehicle){
            id = document.id
            rego = vehicleRego.text.toString()
            description = vehicleDescripion.text.toString()
            kms = vehicleKms.text.toString().toInt()
            model = vehicleModelSpinner.selectedItem.toString()
            yearModel = vehicleYearModel.text.toString().toInt()
        }



        val set = document.set(vehicle)
        set.addOnSuccessListener {
            Log.d("Firebase", "Vehicle Saved")
            (activity as MainActivity).showMainFragment()
        }
        set.addOnFailureListener {
            Log.d("firestore", "Vehicle not saved")
        }
    }

}