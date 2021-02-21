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

        clearFields()

        vehicleModelSpinner.setAdapter(ArrayAdapter.createFromResource(context!!, R.array.vehicle_model,android.R.layout.simple_spinner_item))

        saveRepairBtn.setOnClickListener {

            saveVehicle()
        }

        cmdReturnFromRepairToMain.setOnClickListener {
            clearFields()
            (activity as MainActivity).showMainFragment()
        }

        takePhotoBtn.setOnClickListener {

        }
    }

    private fun clearFields() {
        vehicleRego.text.clear()
        var rego = vehicleRego.text.toString()
        vehicleDescripion.setText("")
        vehicleKms.text.clear()
        //vehicleModelSpinner.selectedItem.toString()
        vehicleYearModel.text.clear()
        vehicleColor.text.clear()
    }

    private fun saveVehicle() {
        var vehicle = Vehicle()

        vehicle.apply{
            rego = vehicleRego.text.toString()
            description = vehicleDescripion.text.toString()
            kms = vehicleKms.text.toString().toInt()
            model = vehicleModelSpinner.selectedItem.toString()
            yearModel = vehicleYearModel.text.toString().toInt()
            color = vehicleColor.text.toString()
        }.apply {
            clearFields()
        }.apply {
            viewModel.saveVehicle(vehicle)
        }.apply {
            (activity as MainActivity).showMainFragment()
        }


        doThis({
            val it = ""
            "something is $it"
        })

    }

    private fun doThis(function: () -> String) {

    }
}