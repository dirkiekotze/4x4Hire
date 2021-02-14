package com.au.a4x4vehiclehirefraser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.au.a4x4vehiclehirefraser.ui.main.MainViewModel
import com.google.firebase.FirebaseApp

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


//    @Test
//    fun useAppContext() {
//
//        //val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.au.a4x4vehiclehirefraser", appContext.packageName)
//    }

    @Test
    fun searchForSetOfPradoFrontRoaters_ReturnSetOfPRadoFrontRoaters() {
        FirebaseApp.initializeApp(appContext);
        givenAFeedOfFrontPradoRoatersAreAvailable()
        whenSearchForSetOfPradoFrontRoaters()
        thenResultContainsSetOfPradoFrontRoaters()
    }

    private fun givenAFeedOfFrontPradoRoatersAreAvailable() {

        mvm = MainViewModel()
    }

    private fun whenSearchForSetOfPradoFrontRoaters() {
        mvm.fetchServiceItem("Prado", "Set of Front Rotors")
    }

    private fun thenResultContainsSetOfPradoFrontRoaters() {
        var pradoFrontSetRoatersFound = false
        mvm.service.observeForever {
            assertNotNull(it)
            assertNotNull(it.size > 0)
            it.forEach {
                if((it.description == "Set of Front Rotors") && (it.vehicleType == "Prado")){
                    pradoFrontSetRoatersFound = true
                    assertTrue(pradoFrontSetRoatersFound)
                }else{
                    assertTrue(pradoFrontSetRoatersFound)

                }
            }

        }


    }
}