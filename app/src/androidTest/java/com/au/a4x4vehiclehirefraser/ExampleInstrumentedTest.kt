package com.au.a4x4vehiclehirefraser

import android.nfc.Tag
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.au.a4x4vehiclehirefraser.dto.ServiceItem
import com.au.a4x4vehiclehirefraser.ui.main.MainViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.add_service_item_fragment.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule
import java.lang.Exception

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    lateinit var mvm: MainViewModel
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var firestore: FirebaseFirestore
    private var description:String = "Test Description"
    private var vehicleType:String = "Test Vehicle"




    @Test
    fun addTestServiceDetail_GetTestServiceDetailBackFromFirestore() {
        FirebaseApp.initializeApp(appContext);
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

        givenWhenWeAddTestDataServiceRecord(vehicleType,description)
        //whenSearchForTheTestDataServiceRecord(vehicleType,description)
        //thenResultContainsSetOfPradoFrontRotors()

    }

    @Test
    fun deleteAddedTestData(){

        try{
            FirebaseApp.initializeApp(appContext);
            firestore = FirebaseFirestore.getInstance()
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
            Log.d("test","In deleteAddedTestData()")
            givenServiceTestRecord(vehicleType,description)
            whenYouDeleteTheServiceEntry()
            //thenTheTestEntryShouldBeGone()
        }catch(exception:Error){
            Log.d("test",exception.message.toString())
        }

    }

    private fun givenServiceTestRecord(vehicleType: String, description: String) {
        addServiceRecord(description, vehicleType)
    }

    private fun whenYouDeleteTheServiceEntry() {

        try{
            var value:String = ""
            Log.d("test","In whenYouDeleteTheServiceEntry()")
            firestore.collection("service")
                .whereEqualTo("vehicleType", vehicleType)
                //.whereEqualTo("description", description)
                .get()
                .addOnSuccessListener {
                    for (document in it.documents) {
                        value = document.get("id").toString()
                    }
                    Log.d("test","In whenYouDeleteTheServiceEntry() Just Before calling mvm.deleteServicePerId")
                    mvm.deleteServicePerId(value)
                    thenTheTestEntryShouldBeGone()
                }
                .addOnFailureListener {
                    assertTrue(1 == 2)
                }
        }catch(err:Exception){
            Log.d("test",err.message.toString())
        }

    }

    private fun thenTheTestEntryShouldBeGone() {
        var value:String = ""
        Log.d("test","In thenTheTestEntryShouldBeGone()")
        firestore.collection("service")
            .whereEqualTo("vehicleType", vehicleType)
            .whereEqualTo("description", description)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    value = document.get("id").toString()
                }
                //Entry found
                assertTrue(1==2)
            }
            .addOnFailureListener {
                //Correct entry wasnt found
                assertTrue(1==1)
            }
    }

    private fun givenWhenWeAddTestDataServiceRecord(vehicleType: String, description: String) {
        addServiceRecord(description, vehicleType)
    }

    private fun addServiceRecord(description: String, vehicleType: String) {
        mvm = MainViewModel()
        val service = ServiceItem()
        service.description = description
        service.vehicleType = vehicleType
        mvm.saveService(service)
    }

    private fun whenSearchForTheTestDataServiceRecord(vehicleType: String, description: String) {
        firestore.collection("Service")
            .whereEqualTo("vehicleType", vehicleType)
            .whereEqualTo("description", description)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    var retValue = document.get("description").toString()
                }
                thenResultContainsTheAddedTestData()
            }
            .addOnFailureListener {
                thenResultContainsTheAddedTestData()
            }
    }

    private fun thenResultContainsTheAddedTestData() {
        var found = false
        mvm.service.observeForever {
            assertNotNull(it)
            assertNotNull(it.size > 0)
            it.forEach {
                if ((it.description == description) && (it.vehicleType == vehicleType)) {
                    found = true
                    assertTrue(found)
                } else {
                    assertTrue(found)

                }
            }
        }
    }



}