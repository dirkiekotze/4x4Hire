package com.au.a4x4vehiclehirefraser.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.dto.ServiceItem
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.Constants
import com.au.a4x4vehiclehirefraser.helper.Constants.REGO
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ID
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.add_service_fragment.serviceSaveBtn
import kotlinx.android.synthetic.main.add_service_item_fragment.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.ArrayList

class AddServiceFragment : HelperFragment() {

    companion object {
        fun newInstance() = AddServiceFragment()
    }

    private lateinit var mainViewModel: MainViewModel

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
        mainViewModel.getServiceSaveBtnVisibility(preference.getValueString(SERVICE_ID).toString())
        mainViewModel.getAddServiceItemBtnVisibility(preference.getValueString(SERVICE_ID).toString())

        mainViewModel.serviceSaveBtnVisibility.observe(viewLifecycleOwner, Observer { value ->
            value?.getContentIfNotHandledOrReturnNull()?.let {
                serviceSaveBtn.visibility = it
            }
        })

        mainViewModel.displayServiceAndItems.observe(viewLifecycleOwner, Observer { value ->
            value?.getContentIfNotHandledOrReturnNull()?.let {
                displayService()
                displayServiceItems()
            }
        })

        mainViewModel.addServiceItemBtnVisibility.observe(viewLifecycleOwner, Observer { value ->
            value?.getContentIfNotHandledOrReturnNull()?.let {
                addServiceItemBtn.visibility = it
            }
        })


        serviceSaveBtn.setOnClickListener {
            saveService()
        }

        serviceBack.setOnClickListener {
            //Stop
            preference.save(SERVICE_ID, "")
            preference.save(SERVICE_ID, "")
            clearFields()
            (activity as MainActivity).showMainFragment()
        }

        //Callback from MainFragment via LifeData
        mainViewModel.addServiceId.observe(viewLifecycleOwner, Observer { id ->
            id?.getContentIfNotHandledOrReturnNull()?.let {
                var xx = it
                preference.save(SERVICE_ID, it)
                addServiceItemBtn.visibility = View.VISIBLE

            }
        })

        mainViewModel.showServiceDetail.observe(viewLifecycleOwner, Observer { service ->
            service?.getContentIfNotHandledOrReturnNull()?.let {
                var service = it
                with(it) {
                    service_price.setText(price.toString())
                    service_date.setText(date)
                    service_description.setText(description)
                    service_kms.setText(kms.toString())
                }


            }
        })

        mainViewModel.showServiceItems.observe(viewLifecycleOwner, Observer { service ->
            service?.getContentIfNotHandledOrReturnNull()?.let {
                rcyServiceItem.visibility = View.VISIBLE
                rcyServiceItem.hasFixedSize()
                rcyServiceItem.layoutManager = LinearLayoutManager(context)
                rcyServiceItem.itemAnimator = DefaultItemAnimator()
                rcyServiceItem.adapter = ServiceItemAdapter(it, R.layout.add_service_item_row)

            }
        })

        addServiceItemBtn.setOnClickListener {
            (activity as MainActivity).showServiceItemFragment()
        }


    }

    private fun displayService() {
        //This calls
        //mainViewModel.showServiceDetail.observe
        mainViewModel.getService(preference.getValueString(SERVICE_ID).toString())

    }

    private fun displayServiceItems() {
        //This will be the callback from the ViewModel
        //mainViewModel.showServiceItems.observe
        mainViewModel.getServiceItem(preference.getValueString(SERVICE_ID).toString())
    }


    private fun clearFields() {
        service_description.setText("")
        service_date.setText("")
        service_kms.setText("")
    }

    private fun saveService() {

        val service = Service()
        with(service) {
            description = service_description.text.toString()
            kms = service_kms.text.toString().toDouble()
            date = service_date.text.toString()
            rego = preference.getValueString(REGO)!!
        }
        mainViewModel.saveService(service)

    }
}