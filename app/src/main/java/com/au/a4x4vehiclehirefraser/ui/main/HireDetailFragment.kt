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
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.helper.Constants
import com.au.a4x4vehiclehirefraser.helper.Helper.toast
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.hire_detail_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class HireDetailFragment : HelperFragment() {

    var _cal = Calendar.getInstance()

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


        hireAddBtn.setOnClickListener {
            "Clicked on Add".toast(context!!,false)
        }

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
            DatePickerDialog(requireContext(),
                dateSetStartDateListener,
                _cal.get(Calendar.YEAR),
                _cal.get(Calendar.MONTH),
                _cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        hire_end_date.setOnClickListener {
            DatePickerDialog(requireContext(),
                dateSetEndDateListener,
                _cal.get(Calendar.YEAR),
                _cal.get(Calendar.MONTH),
                _cal.get(Calendar.DAY_OF_MONTH)).show()
        }


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