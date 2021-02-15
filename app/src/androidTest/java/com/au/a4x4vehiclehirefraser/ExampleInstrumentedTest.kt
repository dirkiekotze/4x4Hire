package com.au.a4x4vehiclehirefraser

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
        whenSearchForTheTestDataServiceRecord(vehicleType,description)
        //thenResultContainsSetOfPradoFrontRotors()

    }

    private fun givenWhenWeAddTestDataServiceRecord(vehicleType: String, description: String) {
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


    private fun givenAFeedOfFrontPradoRoatersAreAvailable() {

        mvm = MainViewModel()
        val service = ServiceItem()
        with(service) {
            id = ""
            description = description
            vehicleType = vehicleType
        }
        mvm.saveService(service)
    }

    private fun whenSearchForSetOfPradoFrontRoaters() {

        firestore.collection("Service")
            .whereEqualTo("vehicleType", vehicleType)
            .whereEqualTo("description", description)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    var retValue = document.get("description").toString()
                }
                thenResultContainsSetOfPradoFrontRotors()
            }
            .addOnFailureListener {

            }
    }

    private fun thenResultContainsSetOfPradoFrontRotors() {
        var pradoFrontSetRoatersFound = false
        mvm.service.observeForever {
            assertNotNull(it)
            assertNotNull(it.size > 0)
            it.forEach {
                if ((it.description == description) && (it.vehicleType == vehicleType)) {
                    pradoFrontSetRoatersFound = true
                    assertTrue(pradoFrontSetRoatersFound)
                } else {
                    assertTrue(pradoFrontSetRoatersFound)

                }
            }
        }
    }
}