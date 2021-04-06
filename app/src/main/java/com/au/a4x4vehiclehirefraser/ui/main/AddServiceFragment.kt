package com.au.a4x4vehiclehirefraser.ui.main

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.helper.Constants
import com.au.a4x4vehiclehirefraser.helper.Constants.REGO
import com.au.a4x4vehiclehirefraser.helper.Constants.REQUIRED
import com.au.a4x4vehiclehirefraser.helper.Constants.REQUIRED_COMPLETE
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.SUCCESSFULLY_ADDED_SERVICE
import com.au.a4x4vehiclehirefraser.helper.Helper.textIsEmpty
import com.au.a4x4vehiclehirefraser.helper.Helper.toMillis
import com.au.a4x4vehiclehirefraser.helper.Helper.toast
import com.au.a4x4vehiclehirefraser.helper.Helper.validate
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.add_service_fragment.addServiceBtn
import kotlinx.android.synthetic.main.add_service_fragment.service_date
import kotlinx.android.synthetic.main.add_service_fragment.service_description
import kotlinx.android.synthetic.main.add_service_fragment.service_kms
import kotlinx.android.synthetic.main.add_service_item_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class AddServiceFragment : HelperFragment() {

    var _cal = Calendar.getInstance()
    var _valid = false
    var _milliseconds:Long = 0
    var _serviceId = ""

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
                addServiceBtn.visibility = it
            }
        })

        mainViewModel.serviceSaveBtnText.observe(viewLifecycleOwner, Observer { value ->
            value?.getContentIfNotHandledOrReturnNull()?.let {
                addServiceBtn.setText(it)
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
                preference.save(SERVICE_ID, it)
                addServiceItemBtn.visibility = View.VISIBLE
                addServiceBtn.setText(R.string.edit_service)
                SUCCESSFULLY_ADDED_SERVICE.toast(context!!,false)

            }
        })

        mainViewModel.showServiceDetail.observe(viewLifecycleOwner, Observer { service ->
            service?.getContentIfNotHandledOrReturnNull()?.let {
                var service = it
                with(it) {
                    _serviceId = service.id
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

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,dayOfMonth: Int) {
                _cal.set(Calendar.YEAR, year)
                _cal.set(Calendar.MONTH, monthOfYear)
                _cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        service_date.setFocusable(false);
        service_date.setKeyListener(null);

        service_date.setOnClickListener {

            DatePickerDialog(requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                _cal.get(Calendar.YEAR),
                _cal.get(Calendar.MONTH),
                _cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        service_date.validate(REQUIRED) {s -> s.textIsEmpty()}
        service_description.validate(REQUIRED) {s -> s.textIsEmpty()}
        service_kms.validate(REQUIRED){s -> s.textIsEmpty()}
        service_price.validate(REQUIRED){s -> s.textIsEmpty()}

        mainViewModel.validToAddService.observe(viewLifecycleOwner, Observer { valid ->
            valid?.getContentIfNotHandledOrReturnNull()?.let {

                if(it){
                    saveService()
                }else{
                    REQUIRED_COMPLETE.toString().toast(context!!, false)
                }

            }
        })

        mainViewModel.displayToast.observe(viewLifecycleOwner, Observer { message ->
            message?.getContentIfNotHandledOrReturnNull()?.let {
                it.toast(context!!,false)
                startActivity(Intent(activity,MainActivity::class.java))
            }
        })

        addServiceBtn.setOnClickListener {
            mainViewModel.validateService(service_date.text.length,service_description.text.length,service_kms.text.length,service_price.text.length)
        }


        serviceDelete.setOnClickListener {
            showDialog()

        }

    }



    private fun deleteService() {
        mainViewModel.deleteServicePerId(preference.getValueString(SERVICE_ID)!!)
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
                DialogInterface.BUTTON_POSITIVE -> deleteService()
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

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        service_date!!.setText(sdf.format(_cal.getTime()))
        _milliseconds = _cal.timeInMillis
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
            id = _serviceId
            description = service_description.text.toString()
            kms = service_kms.text.toString().toDouble()
            date = service_date.text.toString()
            rego = preference.getValueString(REGO)!!
            dateMilliseconds = _milliseconds
            price = service_price.text.toString().toDouble()
        }
        mainViewModel.saveService(service)

    }
}