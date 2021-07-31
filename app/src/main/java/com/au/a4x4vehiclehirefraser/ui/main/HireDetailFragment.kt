package com.au.a4x4vehiclehirefraser.ui.main

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Hire
import com.au.a4x4vehiclehirefraser.helper.Constants
import com.au.a4x4vehiclehirefraser.helper.Constants.HIRE_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.REGO
import com.au.a4x4vehiclehirefraser.helper.Constants.SUCCESS_HIRE
import com.au.a4x4vehiclehirefraser.helper.Helper.textIsEmpty
import com.au.a4x4vehiclehirefraser.helper.Helper.toMillis
import com.au.a4x4vehiclehirefraser.helper.Helper.toast
import com.au.a4x4vehiclehirefraser.helper.Helper.validate
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import kotlinx.android.synthetic.main.add_service_fragment.service_Recycler_Header
import kotlinx.android.synthetic.main.hire_detail_fragment.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HireDetailFragment : HelperFragment() {

    var _calStart = Calendar.getInstance()
    var _calEnd = Calendar.getInstance()
    var _hireId = ""
    val df: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    companion object {
        fun newInstance() = HireDetailFragment()
    }

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.hire_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity.let {
            mainViewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }

        clearFields()

        preference = SharedPreference(requireContext())
        setupDateFields()

        if (!preference.getValueString(HIRE_ID).isNullOrEmpty()) {
            showHire()
        }

        hireAddBtn.setOnClickListener {
            mainViewModel.validateHire(
                hire_start_date.text.length,
                hire_end_date.text.length,
                hire_days.text.length,
                hire_name.text.length,
                hire_email.text.length,
                hire_note.text.length,
                hire_kms.text.length
            )
        }

        hireDeleteBtn.setOnClickListener {
            showDialog()
        }

        hireBack.setOnClickListener {
            clearFields()
            (activity as MainActivity).showMainFragment()
        }

        mainViewModel.showHireDetailSingle.observe(viewLifecycleOwner, Observer { hire ->
            hire?.getContentIfNotHandledOrReturnNull()?.let {
                with(it) {
                    _hireId = id
                    _calStart.timeInMillis = df.parse(startDate).toMillis()
                    hire_start_date.setText(startDate)
                    _calEnd.timeInMillis = df.parse(endDate).toMillis()
                    hire_end_date.setText(endDate)
                    hire_days.setText(days.toString())
                    hire_name.setText(name)
                    hire_email.setText(email)
                    hire_note.setText(note)
                    hire_price.setText(price.toString())
                    hire_kms.setText(kms.toString())
                    preference.save(HIRE_ID, id)
                }
            }
        })

        mainViewModel.validToAddHire.observe(viewLifecycleOwner, Observer { valid ->
            valid?.getContentIfNotHandledOrReturnNull()?.let {

                if (it) {
                    saveHire()
                } else {
                    Constants.REQUIRED_COMPLETE.toString().toast(context!!, false)
                }

            }
        })

        mainViewModel.hireId.observe(viewLifecycleOwner, Observer { id ->
            id?.getContentIfNotHandledOrReturnNull()?.let {
                "$SUCCESS_HIRE $it".toast(context!!, false)
                preference.save(Constants.HIRE_ID, 0)
                startActivity(Intent(activity, MainActivity::class.java))

            }
        })

        mainViewModel.hideAllWithMessage.observe(viewLifecycleOwner, Observer { text ->
            text?.getContentIfNotHandledOrReturnNull()?.let {
                it.toast(context!!, false)

                //ToDo:Move to Model
                if (service_Recycler_Header != null) {
                    service_Recycler_Header.visibility = View.GONE
                    rcyService.visibility = View.GONE
                }
                if (hire_Recycler_Header != null) {
                    hire_Recycler_Header.visibility = View.GONE
                    rcyHire.visibility = View.GONE
                }


            }
        })

        mainViewModel.displayToast.observe(viewLifecycleOwner, Observer { message ->
            message?.getContentIfNotHandledOrReturnNull()?.let {
                it.toast(context!!, false)
                (activity as MainActivity).showMainFragment()
            }
        })


        val dateSetStartDateListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                _calStart.set(Calendar.YEAR, year)
                _calStart.set(Calendar.MONTH, monthOfYear)
                _calStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateStartDate()
            }
        }

        val dateSetEndDateListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                _calEnd.set(Calendar.YEAR, year)
                _calEnd.set(Calendar.MONTH, monthOfYear)
                _calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateEndDate()
            }
        }

        hire_start_date.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetStartDateListener,
                _calStart.get(Calendar.YEAR),
                _calStart.get(Calendar.MONTH),
                _calStart.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        hire_end_date.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetEndDateListener,
                _calEnd.get(Calendar.YEAR),
                _calEnd.get(Calendar.MONTH),
                _calEnd.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    private fun doDelete(hireId: String?) {

        mainViewModel.deleteHire(hireId)
    }

    private fun showHire() {
        mainViewModel.getHirePerId(preference.getValueString(HIRE_ID)!!)
    }

    // Method to show an alert dialog with yes, no and cancel button
    private fun showDialog() {
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context)

        // Set a title for alert dialog
        builder.setTitle("Delete Hire")

        // Set a message for alert dialog
        builder.setMessage("Do you want to delete the selected Hire.")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> doDelete(preference.getValueString(HIRE_ID))
            }
        }


        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO", dialogClickListener)


        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    private fun clearFields() {
        hire_price.setText("")
        hire_start_date.setText("")
        hire_end_date.setText("")
        hire_note.setText("")
        hire_email.setText("")
        hire_name.setText("")
        hire_days.setText("")
        hire_kms.setText("")
    }

    private fun setupDateFields() {
        hire_start_date.setFocusable(false);
        hire_start_date.setKeyListener(null);
        hire_end_date.setFocusable(false);
        hire_end_date.setKeyListener(null);
    }

    private fun saveHire() {
        val hire = Hire()
        with(hire) {
            id = _hireId
            milliseconds = System.currentTimeMillis()
            startDate = hire_start_date.text.toString()
            endDate = hire_end_date.text.toString()
            name = hire_name.text.toString()
            email = hire_email.text.toString()
            note = hire_note.text.toString()
            price = hire_price.text.toString().toDouble()
            days = hire_days.text.toString().toInt()
            rego = preference.getValueString(REGO)!!
            kms = hire_kms.text.toString().toInt()

        }
        mainViewModel.saveHire(hire)
        clearFields()
    }


    private fun doValidation() {
        hire_start_date.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_end_date.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_name.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_email.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_note.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_price.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_days.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_kms.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
    }

    private fun updateStartDate() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        hire_start_date!!.setText(sdf.format(_calStart.getTime()))
        //preference.save(Constants.MILLISECONDS,_cal.timeInMillis.toString())
    }

    private fun updateEndDate() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        hire_end_date!!.setText(sdf.format(_calStart.getTime()))
        //preference.save(Constants.MILLISECONDS,_cal.timeInMillis.toString())
    }

}