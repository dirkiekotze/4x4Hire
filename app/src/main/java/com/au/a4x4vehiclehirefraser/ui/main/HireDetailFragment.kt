package com.au.a4x4vehiclehirefraser.ui.main

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Hire
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.helper.Constants
import com.au.a4x4vehiclehirefraser.helper.Constants.REGO
import com.au.a4x4vehiclehirefraser.helper.Constants.SUCCESS_HIRE
import com.au.a4x4vehiclehirefraser.helper.Helper.textIsEmpty
import com.au.a4x4vehiclehirefraser.helper.Helper.toast
import com.au.a4x4vehiclehirefraser.helper.Helper.validate
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.hire_detail_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class HireDetailFragment : HelperFragment() {

    var _cal = Calendar.getInstance()
    var _hireId = ""

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

        preference = SharedPreference(requireContext())
        setupDateFields()

        hireAddBtn.setOnClickListener {
            mainViewModel.validateHire(
                hire_start_date.text.length,
                hire_end_date.text.length,
                hire_name.text.length,
                hire_email.text.length,
                hire_note.text.length
            )
        }

        hireBack.setOnClickListener {
            clearFields()
            (activity as MainActivity).showMainFragment()
        }

        mainViewModel.validToAddHire.observe(viewLifecycleOwner, Observer { valid ->
            valid?.getContentIfNotHandledOrReturnNull()?.let {

                if (it) {
//                    preference.save(Constants.SERVICE_ID,"")
//                    preference.save(Constants.SERVICE_ITEM_ID,"")
                    saveHire()
                } else {
                    Constants.REQUIRED_COMPLETE.toString().toast(context!!, false)
                }

            }
        })

        mainViewModel.hireId.observe(viewLifecycleOwner, Observer { id ->
            id?.getContentIfNotHandledOrReturnNull()?.let {
                "$SUCCESS_HIRE $it".toast(context!!, false)

            }
        })


        val dateSetStartDateListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                _cal.set(Calendar.YEAR, year)
                _cal.set(Calendar.MONTH, monthOfYear)
                _cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateStartDate()
            }
        }

        val dateSetEndDateListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                _cal.set(Calendar.YEAR, year)
                _cal.set(Calendar.MONTH, monthOfYear)
                _cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateEndDate()
            }
        }

        hire_start_date.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetStartDateListener,
                _cal.get(Calendar.YEAR),
                _cal.get(Calendar.MONTH),
                _cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        hire_end_date.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetEndDateListener,
                _cal.get(Calendar.YEAR),
                _cal.get(Calendar.MONTH),
                _cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        doValidation()

    }

    private fun clearFields() {
        hire_price.setText("")
        hire_start_date.setText("")
        hire_end_date.setText("")
        hire_note.setText("")
        hire_email.setText("")
        hire_name.setText("")
        hire_days.setText("")
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
            //id = _serviceId
            id = ""
            milliseconds = System.currentTimeMillis()
            startDate = hire_start_date.text.toString()
            endDate = hire_end_date.text.toString()
            name = hire_name.text.toString()
            email = hire_email.text.toString()
            note = hire_note.text.toString()
            price = hire_price.text.toString().toDouble()
            days = hire_days.text.toString().toInt()
            rego = preference.getValueString(REGO)!!

        }
        mainViewModel.saveHire(hire)
    }


    private fun doValidation() {
        hire_start_date.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_end_date.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_name.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_email.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_note.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_price.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        hire_days.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
    }

    private fun updateStartDate() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        hire_start_date!!.setText(sdf.format(_cal.getTime()))
        //preference.save(Constants.MILLISECONDS,_cal.timeInMillis.toString())
    }

    private fun updateEndDate() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        hire_end_date!!.setText(sdf.format(_cal.getTime()))
        //preference.save(Constants.MILLISECONDS,_cal.timeInMillis.toString())
    }

}