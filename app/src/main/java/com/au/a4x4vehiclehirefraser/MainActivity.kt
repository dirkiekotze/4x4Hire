package com.au.a4x4vehiclehirefraser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.au.a4x4vehiclehirefraser.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var activeFragment: Fragment
    private lateinit var mainFragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mainFragment = MainFragment.newInstance()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
            activeFragment = mainFragment
        }
    }
}