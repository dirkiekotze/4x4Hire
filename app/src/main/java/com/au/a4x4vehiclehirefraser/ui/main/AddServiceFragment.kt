package com.au.a4x4vehiclehirefraser.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.ServiceEntry
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.add_service_fragment.serviceSaveBtn

class AddServiceFragment : Fragment() {

    companion object {
        fun newInstance() = AddServiceFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var firestore: FirebaseFirestore
    private var vehicleItem = Vehicle();

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_service_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity.let {
            viewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }

        clearFields()

        serviceSaveBtn.setOnClickListener {
            saveService()
        }

        serviceBack.setOnClickListener {
            (activity as MainActivity).showMainFragment()
        }

        viewModel.vehicle.observe(viewLifecycleOwner, Observer { vehicle ->
            service_Vehicle.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    vehicle
                )
            )
        })

        viewModel.service.observe(viewLifecycleOwner, Observer { service ->
            service_description.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    service
                )
            )
        })

        service_Vehicle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                vehicleItem = parent?.getItemAtPosition(position) as Vehicle


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }


        }
    }


    private fun clearFields() {
        service_description.setText("")
        service_price.setText("")
        service_date.setText("")
        service_note.setText("")
    }

    private fun saveService() {

        val document: DocumentReference
        val service = ServiceEntry()

        document = firestore.collection("serviceEntry").document()
        with(service) {
            id = document.id
            description = service_description.text.toString()
            price = service_price.text.toString().toDouble()
            date = service_date.text.toString()
            note = service_note.text.toString()
            vehicle = service_Vehicle.selectedItem.toString()
            rego =  vehicleItem.rego
        }

        val set = document.set(service)
        set.addOnSuccessListener {
            Log.w("Firebase", "ServiceItem Saved")
            (activity as MainActivity).showMainFragment()
        }
        set.addOnFailureListener {
            Log.w("firestore", "ServiceItem not saved")
        }
    }

}