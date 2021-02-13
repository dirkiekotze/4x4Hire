package com.au.a4x4vehiclehirefraser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.au.a4x4vehiclehirefraser.ui.main.MainFragment
import com.au.a4x4vehiclehirefraser.ui.main.RepairFragment
import com.au.a4x4vehiclehirefraser.ui.main.ServiceFragment
import com.au.a4x4vehiclehirefraser.ui.main.VehicleFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    private lateinit var activeFragment: Fragment
    private lateinit var mainFragment: MainFragment
    private lateinit var vehicleFragment: VehicleFragment
    private lateinit var serviceFragment: ServiceFragment
    private lateinit var repairFragment: RepairFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mainFragment = MainFragment.newInstance()
        vehicleFragment = VehicleFragment.newInstance()
        serviceFragment = ServiceFragment.newInstance()
        repairFragment = RepairFragment.newInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
            activeFragment = mainFragment
        }


    }

    internal fun showVehicleFragment(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, vehicleFragment)
            .commitNow()
        activeFragment = vehicleFragment
    }

    internal fun showServiceFragment(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, serviceFragment)
            .commitNow()
        activeFragment = vehicleFragment
    }

    internal fun showRepairFragment(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, repairFragment)
            .commitNow()
        activeFragment = vehicleFragment
    }



    internal fun showMainFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mainFragment)
            .commitNow()
        activeFragment = mainFragment
    }


}