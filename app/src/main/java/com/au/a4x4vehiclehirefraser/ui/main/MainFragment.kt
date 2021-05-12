package com.au.a4x4vehiclehirefraser.ui.main

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.au.a4x4vehiclehirefraser.MainActivity
import com.firebase.ui.auth.AuthUI
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Hire
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.dto.Type
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.Constants.HIRE_DETAILS
import com.au.a4x4vehiclehirefraser.helper.Constants.HIRE_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.NOTHING_TO_DISPLAY
import com.au.a4x4vehiclehirefraser.helper.Constants.REGO
import com.au.a4x4vehiclehirefraser.helper.Constants.REPAIRS_OR_SERVICE
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ITEM_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.TYPE
import com.au.a4x4vehiclehirefraser.helper.Constants.TYPE_INDEX
import com.au.a4x4vehiclehirefraser.helper.Constants.USER_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.VEHICLE_INDEX
import com.au.a4x4vehiclehirefraser.helper.Helper.toast
import com.au.a4x4vehiclehirefraser.helper.OneTimeOnly
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.service_Recycler_Header

class MainFragment : HelperFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var mainViewModel: MainViewModel
    private val AUTH_REQUEST_CODE = 2002
    private var user: FirebaseUser? = null
    var redirectToMainActivity = MutableLiveData<OneTimeOnly<Boolean>>()


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

        mainViewModel.deleteServicePerRego("TestVehicle",false)

        preference = SharedPreference(requireContext())

        if (preference.getValueString(USER_ID).isNullOrEmpty()) {
            doLoginBtn.visibility = View.VISIBLE
            typeSpinner.visibility = View.GONE
            vehicleSpinner.visibility = View.GONE
        } else{
            doLoginBtn.visibility = View.GONE
            typeSpinner.visibility = View.VISIBLE
            vehicleSpinner.visibility = View.VISIBLE
            doStartup()

            //Spinner select
            vehicleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

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
                    preference.save(REGO, vehicle.rego)
                    preference.save(VEHICLE_INDEX, position)
                    if (typeSpinner.selectedItem.toString().equals(REPAIRS_OR_SERVICE)) {
                        displayServices()
                    }else if (typeSpinner.selectedItem.toString().equals(HIRE_DETAILS)){
                        displayHireDetails()
                    }
                }
            }


            typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

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
                    preference.save(TYPE, type.id)
                    preference.save(TYPE_INDEX, position)

                    if (type.value.equals(REPAIRS_OR_SERVICE)) {
                        displayServices()
                    } else if(type.value.equals(HIRE_DETAILS)){
                        displayHireDetails()
                    }
                }

            }
        }


        doLoginBtn.setOnClickListener {
            logon()
        }

        autoComplereServiceSearch.setOnItemClickListener { parent, view, position, id ->
            var selectedService = parent.getItemAtPosition(position) as Service
            autoComplereServiceSearch.setText("")
            navigateToService(selectedService)

        }

        autoComplereHireSearch.setOnItemClickListener { parent, view, position, id ->
            var selectedHire = parent.getItemAtPosition(position) as Hire
            autoComplereHireSearch.setText("")
            navigateToHire(selectedHire)

        }

    }

    private fun doStartup() {
        mainViewModel.type.observe(viewLifecycleOwner, Observer { type ->
            typeSpinner.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    type
                )
            )

            typeSpinner.setSelection(preference.getValueInt(TYPE_INDEX))


        })

        mainViewModel.vehicle.observe(viewLifecycleOwner, Observer { vehicle ->

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

            vehicleSpinner.setSelection(preference.getValueInt(VEHICLE_INDEX))

        })

        mainViewModel.hideAllWithMessage.observe(viewLifecycleOwner, Observer { text ->
            text?.getContentIfNotHandledOrReturnNull()?.let {
                it.toast(context!!, false)
                service_Recycler_Header.visibility = View.GONE
                rcyService.visibility = View.GONE

                hire_Recycler_Header.visibility = View.GONE
                rcyHire.visibility = View.GONE
            }
        })

        mainViewModel.showServiceDetailPerRego.observe(viewLifecycleOwner, Observer { serviceList ->
            serviceList?.getContentIfNotHandledOrReturnNull()?.let {
                removePrevious()
                if (it.size == 0) {
                    //makeToast(context, false, "Nothing to Display")
                    NOTHING_TO_DISPLAY.toString().toast(context!!, false)
                    service_Recycler_Header.visibility = View.GONE
                    hire_Recycler_Header.visibility = View.GONE
                    serviceSearchWrapper.visibility = View.GONE
                } else {
                    service_Recycler_Header.visibility = View.VISIBLE
                    hire_Recycler_Header.visibility = View.GONE
                    serviceSearchWrapper.visibility = View.VISIBLE
                    rcyService.visibility = View.VISIBLE
                    rcyService.hasFixedSize()
                    rcyService.layoutManager = LinearLayoutManager(context)
                    rcyService.itemAnimator = DefaultItemAnimator()
                    rcyService.adapter = ServiceAdapter(
                        it,
                        R.layout.add_service_row,
                        onClickListener = { view, service -> openService(view, service) })
                    //Add to Search
                    autoComplereServiceSearch.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, it))
                }

            }
        })

        mainViewModel.showHireDetail.observe(viewLifecycleOwner, Observer { hireList ->
            hireList?.getContentIfNotHandledOrReturnNull()?.let {
                removePrevious()
                if (it.size == 0) {
                    NOTHING_TO_DISPLAY.toString().toast(context!!, false)
                    service_Recycler_Header.visibility = View.GONE
                    hire_Recycler_Header.visibility = View.GONE
                    hireSearchWrapper.visibility = View.GONE

                } else {
                    hire_Recycler_Header.visibility = View.VISIBLE
                    service_Recycler_Header.visibility = View.GONE
                    rcyHire.visibility = View.VISIBLE
                    hireSearchWrapper.visibility = View.VISIBLE
                    rcyHire.hasFixedSize()
                    rcyHire.layoutManager = LinearLayoutManager(context)
                    rcyHire.itemAnimator = DefaultItemAnimator()
                    rcyHire.adapter = HireAdapter(
                        it,
                        R.layout.hire_row,
                        onClickListener = { view, hire -> openHire(view, hire) })
                    //Add to Search
                    autoComplereHireSearch.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, it))
                }

            }
        })
    }

    private fun removePrevious() {
        service_Recycler_Header.visibility = View.GONE
        rcyService.visibility = View.GONE
        serviceSearchWrapper.visibility = View.GONE

        hire_Recycler_Header.visibility = View.GONE
        rcyHire.visibility = View.GONE
        hireSearchWrapper.visibility = View.GONE
    }


    private fun displayServices() {

        mainViewModel.getServicePerRego(preference.getValueString("rego").toString())
    }

    private fun displayHireDetails(){

        mainViewModel .getHirePerRego(preference.getValueString("rego").toString())
    }



    private fun openService(view: View, service: Service) {
        navigateToService(service)
    }

    private fun navigateToService(service: Service) {
        preference.save(SERVICE_ID, service.id)
        preference.save(SERVICE_ITEM_ID, service.id)
        (activity as MainActivity).showServiceFragment()
    }

    private fun openHire(view: View, hire: Hire) {

        navigateToHire(hire)
    }

    private fun navigateToHire(hire: Hire) {
        preference.save(HIRE_ID, hire.id)
        (activity as MainActivity).showHireFragment()
    }

    private fun logon() {
        var providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), AUTH_REQUEST_CODE
        )
    }

    fun redirectToMainActivity() {
        (activity as MainActivity).redirectToMainFragment()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AUTH_REQUEST_CODE) {
                user = FirebaseAuth.getInstance().currentUser
                preference.save(USER_ID, user!!.uid)
                doLoginBtn.visibility = View.GONE
                redirectToMainActivity()

            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }else{
            "Unable to Login".toast(context!!,false)
        }
    }

}


