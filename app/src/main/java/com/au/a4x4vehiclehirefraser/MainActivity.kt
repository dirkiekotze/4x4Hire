package com.au.a4x4vehiclehirefraser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.au.a4x4vehiclehirefraser.ui.main.MainFragment
import com.au.a4x4vehiclehirefraser.ui.main.RepairFragment
import com.au.a4x4vehiclehirefraser.ui.main.AddServiceItemFragment
import com.au.a4x4vehiclehirefraser.ui.main.AddVehicleFragment

class MainActivity : AppCompatActivity() {

    private lateinit var activeFragment: Fragment
    private lateinit var mainFragment: MainFragment
    private lateinit var vehicleFragment: AddVehicleFragment
    private lateinit var serviceItemFragment: AddServiceItemFragment
    private lateinit var repairFragment: RepairFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mainFragment = MainFragment.newInstance()
        vehicleFragment = AddVehicleFragment.newInstance()
        serviceItemFragment = AddServiceItemFragment.newInstance()
        repairFragment = RepairFragment.newInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
            activeFragment = mainFragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_vehicle){
            showVehicleFragment()
        } else if(item.itemId == R.id.add_repair){
            showRepairFragment()
        } else if(item.itemId == R.id.add_service_item){
            showServiceItemFragment()
        }
        return true
    }

    internal fun showVehicleFragment(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, vehicleFragment)
            .commitNow()
        activeFragment = vehicleFragment
    }

    internal fun showServiceItemFragment(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, serviceItemFragment)
            .commitNow()
        activeFragment = serviceItemFragment
    }

    internal fun showRepairFragment(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, repairFragment)
            .commitNow()
        activeFragment = repairFragment
    }



    internal fun showMainFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mainFragment)
            .commitNow()
        activeFragment = mainFragment
    }


}