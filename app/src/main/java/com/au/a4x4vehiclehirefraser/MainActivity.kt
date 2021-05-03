package com.au.a4x4vehiclehirefraser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ITEM_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.TYPE_INDEX
import com.au.a4x4vehiclehirefraser.helper.Constants.VEHICLE_INDEX
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import com.au.a4x4vehiclehirefraser.ui.main.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.lang.reflect.Array.get

class MainActivity : AppCompatActivity() {

    private lateinit var activeFragment: Fragment
    private lateinit var mainFragment: MainFragment
    private lateinit var addVehicleFragment: AddVehicleFragment
    private lateinit var addServiceFragment: AddServiceFragment
    private lateinit var hireDetailsFragment: HireDetailFragment
    private lateinit var serviceItemFragment: AddServiceItemFragment
    private lateinit var repairFragment: RepairFragment
    private lateinit var mainViewModel: MainViewModel
    protected lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        preference = SharedPreference(this)
        clearPreferences()
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainFragment = MainFragment.newInstance()
        addVehicleFragment = AddVehicleFragment.newInstance()
        addServiceFragment = AddServiceFragment.newInstance()
        serviceItemFragment = AddServiceItemFragment.newInstance()
        hireDetailsFragment = HireDetailFragment.newInstance()
        repairFragment = RepairFragment.newInstance()


        //if (savedInstanceState == null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
        activeFragment = mainFragment
        //}

    }

    private fun clearPreferences() {
//        preference.save(VEHICLE_INDEX,0)
//        preference.save(TYPE_INDEX,0)
//        preference.save(SERVICE_ID,0)
//        preference.save(SERVICE_ITEM_ID,0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_vehicle) {
            if (activeFragment != addVehicleFragment) {
                showVehicleFragment()
            }
        } else if (item.itemId == R.id.add_service) {
            if (activeFragment != addServiceFragment) {
                preference.save(SERVICE_ID,"")
                preference.save(SERVICE_ITEM_ID,"")
                showServiceFragment()
            }
        }else if(item.itemId == R.id.hire){
            showHireFragment()
        }
        return true
    }



    fun doRediredtToMainActivity() {
        startActivity(Intent(this, MainFragment::class.java))
    }

    internal fun showVehicleFragment() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, addVehicleFragment)
            .commitNow()
        activeFragment = addVehicleFragment
    }

    internal fun showServiceItemFragment() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, serviceItemFragment)
            .commitNow()
        activeFragment = serviceItemFragment
    }

    internal fun showServiceFragment() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, addServiceFragment)
            .commitNow()
        activeFragment = addServiceFragment
    }

    internal fun showHireFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, hireDetailsFragment)
            .commitNow()
        activeFragment = hireDetailsFragment
    }


    internal fun showMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mainFragment)
            .commitNow()
        activeFragment = mainFragment
    }

    internal fun redirectToMainFragment() {
        startActivity(Intent(this, MainActivity::class.java))

    }


}