package com.au.a4x4vehiclehirefraser.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.ServiceItem
import com.au.a4x4vehiclehirefraser.helper.Constants
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ITEM_ID
import com.au.a4x4vehiclehirefraser.helper.Helper.textIsEmpty
import com.au.a4x4vehiclehirefraser.helper.Helper.toast
import com.au.a4x4vehiclehirefraser.helper.Helper.validate
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.add_service_item_fragment.*
import kotlinx.android.synthetic.main.add_service_item_fragment.serviceDescription
import kotlinx.android.synthetic.main.add_service_item_fragment.addServiceBtn

class AddServiceItemFragment : HelperFragment() {

    companion object {
        fun newInstance() = AddServiceItemFragment()
    }

    private lateinit var mainViewModel: MainViewModel
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
            mainViewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }
        preference = SharedPreference(requireContext())
        clearFields()

        mainViewModel.validToAddServiceItem.observe(viewLifecycleOwner, Observer { valid ->
            valid?.getContentIfNotHandledOrReturnNull()?.let {

                if(it){
                    saveServiceItem()
                }else{
                    Constants.REQUIRED_COMPLETE.toString().toast(context!!, false)
                }

            }
        })

        addServiceBtn.setOnClickListener {
            mainViewModel.validateServiceItem(serviceDescription.text.length,servicePrice.text.length,serviceQuantity.text.length)

        }

        serviceBackBtn.setOnClickListener {
            (activity as MainActivity).showMainFragment()
        }

        //Callback from MainFragment via LifeData
        mainViewModel.addServiceItemId.observe(viewLifecycleOwner, Observer { id ->
            id?.getContentIfNotHandledOrReturnNull()?.let {
                preference.save(SERVICE_ITEM_ID, it)
                (activity as MainActivity).showServiceFragment()
            }
        })

        serviceDescription.validate(Constants.REQUIRED) { s -> s.textIsEmpty()}
        servicePrice.validate(Constants.REQUIRED) { s -> s.textIsEmpty()}
        serviceQuantity.validate(Constants.REQUIRED){ s -> s.textIsEmpty()}

    }

    private fun clearFields() {
        serviceDescription.setText("")
        servicePrice.setText("")
        serviceQuantity.setText("")
    }

    private fun saveServiceItem() {

        val serviceItem = ServiceItem()
        with(serviceItem) {
            description = serviceDescription.text.toString()
            price = servicePrice.text.toString().toDouble()
            quantity = serviceQuantity.text.toString()

        }

        serviceItem.serviceId = preference.getValueString(SERVICE_ID).toString()
        mainViewModel.saveServiceItem(serviceItem)

    }

}