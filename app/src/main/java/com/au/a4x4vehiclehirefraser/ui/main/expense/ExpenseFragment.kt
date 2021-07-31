package com.au.a4x4vehiclehirefraser.ui.main.expense

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Expense
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.Constants
import com.au.a4x4vehiclehirefraser.helper.Constants.EXPENSE_ID
import com.au.a4x4vehiclehirefraser.helper.Helper.textIsEmpty
import com.au.a4x4vehiclehirefraser.helper.Helper.toast
import com.au.a4x4vehiclehirefraser.helper.Helper.validate
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import com.au.a4x4vehiclehirefraser.ui.main.HelperFragment
import kotlinx.android.synthetic.main.expense_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log


class ExpenseFragment : HelperFragment(),ExpenseAdapter.OnClickListener {

    var _cal = Calendar.getInstance()
    var _expenseId = ""
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseAdapter


    companion object {
        fun newInstance() = ExpenseFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.expense_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        expenseViewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        preference = SharedPreference(requireContext())
        doValidation()
        adapter = ExpenseAdapter(this)
        listViewExpense.adapter = adapter

        expenseViewModel.displayToast.observe(viewLifecycleOwner, Observer { message ->
            message?.getContentIfNotHandledOrReturnNull()?.let {
                it.toast(context!!, false)
                clearFields()
                (activity as MainActivity).showMainFragment()
            }
        })


        //Validate Callback ==> validToAddExpense
        expenseAddBtn.setOnClickListener {
            expenseViewModel.validateExpense(
                expense_date.text.toString().length,
                expense_price.text.length
            )
        }

        expenseDelete.setOnClickListener {
            deleteDialog()
        }

        expenseBack.setOnClickListener {
            //Stop
            preference.save(Constants.EXPENSE_ID, "")
            clearFields()
            (activity as MainActivity).showMainFragment()
        }

        //Can you go ahead or not
        expenseViewModel.validToAddExpense.observe(viewLifecycleOwner, Observer { valid ->
            valid?.getContentIfNotHandledOrReturnNull()?.let {

                if (it) {
                    preference.save(Constants.EXPENSE_ID, "")
                    saveExpense()
                } else {
                    Constants.REQUIRED_COMPLETE.toString().toast(context!!, false)
                }

            }
        })

        //Added Successfully
        expenseViewModel.addedExpenseId.observe(viewLifecycleOwner, Observer { id ->
            id?.getContentIfNotHandledOrReturnNull()?.let {

                preference.save(Constants.EXPENSE_ID, it)
                Constants.SUCCESSFULLY_ADDED_SERVICE.toast(context!!, false)

            }
        })

        expenseViewModel.vehicle.observe(viewLifecycleOwner, Observer { vehicle ->

            vehicle.sortBy {
                it.YearModel
            }

            vehicleSpinnerExpense.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.spinner_text_size,
                    vehicle
                )
            )

            vehicleSpinnerExpense.setSelection(preference.getValueInt(Constants.VEHICLE_INDEX))

        })

        expenseViewModel.expenseType.observe(viewLifecycleOwner, Observer { type ->

            type.sortBy {
                it.Id
            }

            expenseTypeSpinner.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.spinner_text_size,
                    type
                )
            )

            vehicleSpinnerExpense.setSelection(preference.getValueInt(Constants.VEHICLE_INDEX))

        })

        expenseViewModel.expense.observe(viewLifecycleOwner, Observer {

            listViewExpense.visibility = View.VISIBLE
            listViewExpense.hasFixedSize()
            listViewExpense.layoutManager = LinearLayoutManager(context)
            listViewExpense.itemAnimator = DefaultItemAnimator()
            adapter.setExpense(it)

        })

        expenseViewModel.showExpenseDetail.observe(viewLifecycleOwner, Observer { expense ->
            expense?.getContentIfNotHandledOrReturnNull()?.let {
                var expense = it
                with(it) {
                    _expenseId = Id
                    expense_price.setText(Price.toString())
                    selectSpinnerItemByValue(vehicleSpinnerExpense,Rego)
                    selectSpinnerItemByValue(expenseTypeSpinner,Type)
                    expense_date.setText(Date)
                    expense_note.setText(Note)
                    preference.save(
                        com.au.a4x4vehiclehirefraser.helper.Constants.MILLISECONDS,
                        DateMilliseconds.toString()
                    )
                    preference.save(EXPENSE_ID, Id)
                }
            }
        })

        //Spinner select
        vehicleSpinnerExpense.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

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
                preference.save(Constants.REGO, vehicle.Rego)
                preference.save(Constants.VEHICLE_INDEX, position)

            }
        }

        //Date Stuff
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                _cal.set(Calendar.YEAR, year)
                _cal.set(Calendar.MONTH, monthOfYear)
                _cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        expense_date.setFocusable(false);
        expense_date.setKeyListener(null);

        expense_date.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                _cal.get(Calendar.YEAR),
                _cal.get(Calendar.MONTH),
                _cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    fun selectSpinnerItemByValue(spnr: Spinner, value: String) {
        val adapter: SpinnerAdapter = spnr.adapter as SpinnerAdapter
        Log.d("firestore","In selectSpinnerItemByValue")
        for (position in 0 until adapter.getCount()) {
            if (adapter.getItemId(position).toString() === value) {
                spnr.setSelection(position)
                Log.d("firestore","In selectSpinnerItemByValue value == $value")
                return
            }
        }
    }

    private fun saveExpense() {
        val expense = Expense()
        with(expense) {

            Id = _expenseId
            Type = expenseTypeSpinner.selectedItem.toString()
            Price = expense_price.text.toString().toDouble()
            Rego = preference.getValueString(Constants.REGO)!!
            Date = expense_date.text.toString()
            Note = expense_note.text.toString()
            DateMilliseconds = preference.getValueString(Constants.MILLISECONDS)!!.toLong()

        }
        expenseViewModel.saveExpense(expense)
    }

    private fun clearFields() {
        expense_price.setText("")
        expense_date.setText("")
        expense_note.setText("")
    }

    private fun doValidation() {
        expense_price.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }
        expense_date.validate(Constants.REQUIRED) { s -> s.textIsEmpty() }

    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        expense_date!!.setText(sdf.format(_cal.getTime()))
        preference.save(Constants.MILLISECONDS, _cal.timeInMillis.toString())
    }

    override fun onClick(id: String) {
        expenseViewModel.getExpensePerId(id)
    }

    // Method to show an alert dialog with yes, no and cancel button
    private fun deleteDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context)

        // Set a title for alert dialog
        builder.setTitle("Are you sure ?.")

        // Set a message for alert dialog
        builder.setMessage("Do you want to delete the selected Expense.")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> deleteExpense()
                //DialogInterface.BUTTON_NEGATIVE -> toast("No.")

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

    private fun deleteExpense() {
        expenseViewModel.deleteExpensePerId(preference.getValueString(EXPENSE_ID)!!)
    }

}