package com.au.a4x4vehiclehirefraser.ui.main

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.au.a4x4vehiclehirefraser.MainActivity
import com.firebase.ui.auth.AuthUI
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Photo
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.dto.Type
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.add_vehicle_fragment.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.ArrayList

class MainFragment : HelperFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var mainViewModel: MainViewModel
    private val AUTH_REQUEST_CODE = 2002
    private var user: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        activity.let {
            mainViewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }

        preference = SharedPreference(requireContext())

        mainViewModel.type.observe(viewLifecycleOwner, Observer { type ->
            typeSpinner.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    type
                )
            )
        })

        mainViewModel.vehicle.observe(viewLifecycleOwner, Observer { vehicle ->

            //Add default value for Spinner
            vehicle.add(Vehicle(description = "Select Vehicle",yearModel = -1))
            vehicle.sortBy {
                it.yearModel
            }

            vehicleSpinner.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    vehicle
                )
            )
        })


        //Spinner select
        vehicleSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var vehicle = parent?.getItemAtPosition(position) as Vehicle
                preference.save("vehicleRego",vehicle.rego)
            }

        }


        typeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var type = parent?.getItemAtPosition(position) as Type
                preference.save("type",type.id)

                if(type.value.equals("Vehicle Service")){
                    displayServices()
                }
            }

        }

        if((preference.getValueString("userId") == null) || (preference.getValueString("userId") == "")){
            logon();
        }

    }

    private fun displayServices() {

        rcyService.visibility = View.VISIBLE
        rcyService.hasFixedSize()
        rcyService.layoutManager = LinearLayoutManager(context)
        rcyService.itemAnimator = DefaultItemAnimator()

        var serviceArrayList = ArrayList<Service>()
        mainViewModel.firestore.collection("service")
            .whereEqualTo("rego",preference.getValueString("vehicleRego"))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    serviceArrayList.add(
                        Service(
                            id = document.get("id").toString(),
                            date = document.get("date").toString(),
                            description =  document.get("description").toString(),
                            kms = document.get("kms").toString().toDouble(),
                            rego = document.get("rego").toString()
                        )
                    )
                }
                rcyService.adapter = ServiceAdapter(serviceArrayList,
                    R.layout.add_service_row,
                    onClickListener = { view, service -> openService(view, service) })
            }
            .addOnFailureListener {
                Log.d("firestore", "Unable to find Service in Firestore:")
            }

    }

    private fun openService(view: View, service: Service) {

        var xx = service.description
        preference.save("serviceId",service.id)
        preference.save("serviceItemId",service.id)
        (activity as MainActivity).showServiceFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AUTH_REQUEST_CODE) {
                user = FirebaseAuth.getInstance().currentUser
                preference.save("userId", user!!.uid)

            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }



    private fun logon() {
        var providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build(), AUTH_REQUEST_CODE
        )
    }

}