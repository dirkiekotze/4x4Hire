package com.au.a4x4vehiclehirefraser.ui.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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

        if(!preference.getValueString(SERVICE_ITEM_ID).isNullOrEmpty()){
            getServiceItem(preference.getValueString(SERVICE_ITEM_ID))
        }

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
            (activity as MainActivity).showServiceFragment()
        }

        //Callback from MainFragment via LifeData
        mainViewModel.addServiceItemId.observe(viewLifecycleOwner, Observer { id ->
            id?.getContentIfNotHandledOrReturnNull()?.let {
                preference.save(SERVICE_ITEM_ID, it)
                (activity as MainActivity).showServiceFragment()
            }
        })

        mainViewModel.showServiceItem.observe(viewLifecycleOwner, Observer { serviceItem ->
            serviceItem?.getContentIfNotHandledOrReturnNull()?.let {
                serviceDescription.setText(it.description)
                servicePrice.setText(it.price.toString())
                serviceQuantity.setText(it.quantity)
                preference.save("SERVICE_ITEM_ID",it.id)
            }
        })

        mainViewModel.deletedServiceItem.observe(viewLifecycleOwner, Observer { message ->
            message?.getContentIfNotHandledOrReturnNull()?.let {
                it.toast(context!!,false)
                preference.save(SERVICE_ITEM_ID,0)
                startActivity(Intent(activity,MainActivity::class.java))
            }
        })

        mainViewModel.displayToast.observe(viewLifecycleOwner, Observer { message ->
            message?.getContentIfNotHandledOrReturnNull()?.let {
                it.toast(context!!,false)

            }
        })

        deleteServiveItemBtn.setOnClickListener {
            showDialog()
        }

        serviceDescription.validate(Constants.REQUIRED) { s -> s.textIsEmpty()}
        servicePrice.validate(Constants.REQUIRED) { s -> s.textIsEmpty()}
        serviceQuantity.validate(Constants.REQUIRED){ s -> s.textIsEmpty()}

    }

    private fun deleteServiceItem() {
        mainViewModel.deleteServiceItem(preference.getValueString(SERVICE_ITEM_ID))
    }

    // Method to show an alert dialog with yes, no and cancel button
    private fun showDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context)

        // Set a title for alert dialog
        builder.setTitle("Are you sure ?.")

        // Set a message for alert dialog
        builder.setMessage("Do you want to delete the selected Service.")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> deleteServiceItem()
                //DialogInterface.BUTTON_NEGATIVE -> toast("No.")

            }
        }


        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO",dialogClickListener)


        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }



    private fun getServiceItem(id: String?) {

        mainViewModel.getServiceItemPerId(id!!)

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
            id = preference.getValueString(SERVICE_ITEM_ID)!!
        }

        serviceItem.serviceId = preference.getValueString(SERVICE_ID).toString()
        mainViewModel.saveServiceItem(serviceItem)

    }

}