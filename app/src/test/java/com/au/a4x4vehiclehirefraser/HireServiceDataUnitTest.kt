package com.au.a4x4vehiclehirefraser

import androidx.lifecycle.MutableLiveData
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.au.a4x4vehiclehirefraser.ui.main.MainViewModel
import com.google.firebase.FirebaseApp
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class HireServiceDataUnitTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    lateinit var mvm: MainViewModel

    @Test
    fun searchForSetOfPradoFrontRoaters_ReturnSetOfPRadoFrontRoaters() {
        givenAFeedOfFrontPradoRoatersAreAvailable()
        whenSearchForSetOfPradoFrontRoaters()
        thenResultContainsSetOfPradoFrontRoaters()
    }

    private fun givenAFeedOfFrontPradoRoatersAreAvailable() {

        mvm = MainViewModel()
    }

    private fun whenSearchForSetOfPradoFrontRoaters() {
        mvm.fetchServiceItem("Prado", "Set of Front Roaters")
    }

    private fun thenResultContainsSetOfPradoFrontRoaters() {
        var pradoFrontSetRoatersFound = false
        mvm.service.observeForever {
            assertNotNull(it)
            assertNotNull(it.size > 0)
            it.forEach {
                if((it.description == "Set of Front Roaters") && (it.vehicleType == "Prado")){
                    pradoFrontSetRoatersFound = true
                }
            }

        }
        assertTrue(pradoFrontSetRoatersFound)

    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}