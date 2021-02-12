package com.au.a4x4vehiclehirefraser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.au.a4x4vehiclehirefraser.ui.main.MainFragment
import com.au.a4x4vehiclehirefraser.ui.main.VehicleFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    private lateinit var activeFragment: Fragment
    private lateinit var mainFragment: MainFragment
    private lateinit var vehicleFragment: VehicleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mainFragment = MainFragment.newInstance()
        vehicleFragment = VehicleFragment.newInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
            activeFragment = mainFragment
        }

        cmdAddVehicleMainFragment.setOnClickListener {

            cmdAddVehicleMainFragment.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, vehicleFragment)
                .commitNow()
            activeFragment = vehicleFragment
        }


    }


}