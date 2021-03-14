package com.au.a4x4vehiclehirefraser.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.add_service_fragment.serviceSaveBtn

class AddServiceFragment : HelperFragment() {

    companion object {
        fun newInstance() = AddServiceFragment()
    }

    private lateinit var mainViewModel: MainViewModel
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
            mainViewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }

        preference = SharedPreference(requireContext())
        clearFields()

        serviceSaveBtn.setOnClickListener {
            saveService()
        }

        serviceBack.setOnClickListener {
            (activity as MainActivity).showMainFragment()
        }

        //Callback from MainFragment via LifeData
        mainViewModel.addServiceId.observe(viewLifecycleOwner, Observer { id ->
            id?.getContentIfNotHandledOrReturnNull()?.let {
                var xx = it
                preference.save("serviceId",it)
                addServiceItemBtn.visibility = View.VISIBLE

            }
        })


//        mainViewModel.vehicle.observe(viewLifecycleOwner, Observer { vehicle ->
//            service_Vehicle.setAdapter(
//                ArrayAdapter(
//                    context!!,
//                    R.layout.support_simple_spinner_dropdown_item,
//                    vehicle
//                )
//            )
//        })

//        mainViewModel.service.observe(viewLifecycleOwner, Observer { service ->
//            service_description.setAdapter(
//                ArrayAdapter(
//                    context!!,
//                    R.layout.support_simple_spinner_dropdown_item,
//                    service
//                )
//            )
//        })

//        service_Vehicle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                vehicleItem = parent?.getItemAtPosition(position) as Vehicle
//
//
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//
//
//        }
    }


    private fun clearFields() {
        service_description.setText("")
        service_date.setText("")
    }

    private fun saveService() {

        val service = Service()
        with(service) {
            description = service_description.text.toString()
            kms = service_kms.text.toString().toDouble()
            date = service_date.text.toString()
            rego = preference.getValueString("vehicleRego")!!
        }

        mainViewModel.saveService(service)

    }

}