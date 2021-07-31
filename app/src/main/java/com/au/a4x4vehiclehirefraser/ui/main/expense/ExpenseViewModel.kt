package com.au.a4x4vehiclehirefraser.ui.main.expense

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.au.a4x4vehiclehirefraser.dto.Expense
import com.au.a4x4vehiclehirefraser.dto.ExpenseType
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.Constants
import com.au.a4x4vehiclehirefraser.helper.OneTimeOnly
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private var firestore: FirebaseFirestore
    var validToAddExpense = MutableLiveData<OneTimeOnly<Boolean>>()
    var addedExpenseId = MutableLiveData<OneTimeOnly<String>>()
    private var _vehicles: MutableLiveData<ArrayList<Vehicle>> = MutableLiveData<ArrayList<Vehicle>>()
    private var _expense: MutableLiveData<ArrayList<Expense>> = MutableLiveData<ArrayList<Expense>>()
    private var _expenseType: MutableLiveData<ArrayList<ExpenseType>> = MutableLiveData<ArrayList<ExpenseType>>()
    var displayToast = MutableLiveData<OneTimeOnly<String>>()
    var showExpenseDetail = MutableLiveData<OneTimeOnly<Expense>>()

    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

        listenToVehicles()
        listenToExpenses()
        listenToExpenseType()
    }

    private fun listenToExpenses() {
        firestore.collection("expense").addSnapshotListener { snapshot, e ->

            //Skip if excepion
            if (e != null) {
                Log.w(ContentValues.TAG, "Listener for Expenses Failed")
                return@addSnapshotListener
            }

            // if we are here, we did not encounter an exception
            if (snapshot != null) {
                val allExpenses = ArrayList<Expense>()
                val documents = snapshot.documents
                documents.forEach {
                    val expense = it.toObject(Expense::class.java)
                    if (expense != null) {
                        allExpenses.add(expense!!)
                    }
                }
                _expense.value = allExpenses
            }
        }
    }

    private fun listenToExpenseType() {
        firestore.collection("expenseType").addSnapshotListener { snapshot, e ->

            //Skip if excepion
            if (e != null) {
                Log.w(ContentValues.TAG, "Listener for ExpenseTypes Failed")
                return@addSnapshotListener
            }

            // if we are here, we did not encounter an exception
            if (snapshot != null) {
                val allExpenseTypes = ArrayList<ExpenseType>()
                val documents = snapshot.documents
                documents.forEach {
                    val expenseType = it.toObject(ExpenseType::class.java)
                    if (expenseType != null) {
                        allExpenseTypes.add(expenseType!!)
                    }
                }
                _expenseType.value = allExpenseTypes
            }
        }
    }

    private fun listenToVehicles() {
        firestore.collection("vehicle").addSnapshotListener { snapshot, e ->

            //Skip if excepion
            if (e != null) {
                Log.w(ContentValues.TAG, "Listener for Vehicle Failed")
                return@addSnapshotListener
            }

            // if we are here, we did not encounter an exception
            if (snapshot != null) {
                val allVehicles = ArrayList<Vehicle>()
                val documents = snapshot.documents
                documents.forEach {
                    val vehicle = it.toObject(Vehicle::class.java)
                    if (vehicle != null) {
                        allVehicles.add(vehicle!!)
                    }
                }
                _vehicles.value = allVehicles
            }
        }
    }

    fun validateExpense(date: Int, price: Int) {

        if( (date > 0) && (price > 0))
        {
            validToAddExpense.value = OneTimeOnly(true)
        }else{
            validToAddExpense.value = OneTimeOnly(false)
        }
    }

    fun saveExpense(expense: Expense) {
        if (!expense.Id.isNullOrEmpty()) {
            updateExpense(expense, expense.Id, true)
        } else {
            firestore.collection("expense")
                .add(expense)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firebase", "Service Saved")
                    addedExpenseId.value = OneTimeOnly(documentReference.id)
                    expense.Id = documentReference.id
                    updateExpense(expense, documentReference.id, false)
                }
                .addOnFailureListener { e ->
                    Log.d("Firebase", "Service not saved")
                }
        }
    }

    private fun updateExpense(expense: Expense, id: String, showToast: Boolean) {
        firestore.collection("expense").document(id)
            .set(expense)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Expense Updated")
                if (showToast) {
                    addedExpenseId.value = OneTimeOnly(id)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Firebase", "Expense Updated")
            }
    }

    fun getExpensePerId(expenseId: String) {

        var expense = Expense()
        firestore.collection("expense").document(expenseId)
            .get()
            .addOnSuccessListener { document ->
                Log.d("firestore", "Select Expense from Firestore for ExpenseId: $expenseId")
                with(expense) {
                    Id = document.get("Id").toString()
                    Type = document.get("Type").toString()
                    Date = document.get("Date").toString()
                    Price = document.get("Price").toString().toDouble()
                    Note = document.get("Note").toString()
                    DateMilliseconds = document.get("DateMilliSeconds").toString().toLong()
                    Rego = document.get("Rego").toString()
                }

                //Callback to AddService
                showExpenseDetail.value = OneTimeOnly(expense)
            }
            .addOnFailureListener {
                Log.d(
                    "firestore",
                    "Unable to select Service from Firestore ServiceId: $expenseId"
                )
            }

    }

    fun deleteExpensePerId(id: String) {

        firestore.collection("expense").whereEqualTo("id", id).get()
            .addOnSuccessListener {
                var batch = firestore.batch();
                it.forEach {
                    //Todo: Test to see if this works
                    it.reference.delete()
                }

                batch.commit();
                Log.w("firestore", "Deleted $id")
                displayToast.value = OneTimeOnly(Constants.DELETED_EXPENSE)

            }
            .addOnFailureListener {
                Log.w("firestore", "Unable to delete $id")
            }


    }

    internal var vehicle: MutableLiveData<ArrayList<Vehicle>>
        get() {
            return _vehicles
        }
        set(value) {
            _vehicles = value
        }

    internal var expense: MutableLiveData<ArrayList<Expense>>
        get() {
            return _expense
        }
        set(value) {
            _expense = value
        }

    internal var expenseType: MutableLiveData<ArrayList<ExpenseType>>
        get() {
            return _expenseType
        }
        set(value) {
            _expenseType = value
        }

}