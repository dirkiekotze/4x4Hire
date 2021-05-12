package com.au.a4x4vehiclehirefraser

import android.nfc.Tag
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.au.a4x4vehiclehirefraser.dto.Service
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
    private var description: String = "Test Description"
    private var vehicleType: String = "Test Vehicle"
    private var rego: String = "TestVehicle"

    @Test
    fun addTestServiceDetail_GetTestServiceDetailBackFromFirestore() {
        FirebaseApp.initializeApp(appContext);
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        mvm = MainViewModel()
        givenWhenWeAddTestDataServiceRecord(vehicleType, rego)
        whenSearchForTheTestDataServiceRecord(rego)
        thenServiceRecordContainsTheAddedTestData()

    }

    @Test
    fun deleteAddedTestData() {

        try {
            FirebaseApp.initializeApp(appContext);
            firestore = FirebaseFirestore.getInstance()
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
            Log.d("test", "In deleteAddedTestData()")
            mvm = MainViewModel()
            givenWhenWeAddTestDataServiceRecord(vehicleType, rego)
            whenYouDeleteTheServiceEntry(rego)
            thenTheTestEntryShouldBeGone()
        } catch (exception: Error) {
            Log.d("test", exception.message.toString())
        }

    }

    private fun givenServiceTestRecord(vehicleType: String, description: String) {
        addServiceRecord(description, vehicleType)
    }

    private fun whenYouDeleteTheServiceEntry(rego:String) {

        mvm.deleteServicePerRego(rego,false)

    }

    private fun thenTheTestEntryShouldBeGone() {
        var vehicleFound = false;
        mvm.vehicle.observeForever {
            // /here is where we do the observing
            assertNotNull(it)
            assertTrue(it.size > 0)
            it.forEach {
                if (it.rego == rego) {
                    vehicleFound = true
                }
            }
            assertFalse(vehicleFound)
        }
    }

    private fun givenWhenWeAddTestDataServiceRecord(vehicleType: String, rego: String) {
        addServiceRecord(description, rego)
    }

    private fun addServiceRecord(description: String, rego: String) {
        val service = Service()
        service.description = description
        service.rego = rego
        service.kms = 1.0
        mvm.saveService(service)
    }

    private fun whenSearchForTheTestDataServiceRecord(rego: String) {
        mvm.getServicePerRego(rego)
    }


    private fun thenServiceRecordContainsTheAddedTestData() {

        var vehicleFound = false;
        mvm.vehicle.observeForever {
            // /here is where we do the observing
            assertNotNull(it)
            assertTrue(it.size > 0)
            it.forEach {
                if (it.rego == rego) {
                    vehicleFound = true
                }
            }
            assertTrue(vehicleFound)
        }

    }


}