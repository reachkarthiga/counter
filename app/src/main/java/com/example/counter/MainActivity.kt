package com.example.counter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.example.counter.counterTag.CounterPage
import com.example.counter.databinding.ActivityMainBinding
import com.example.counter.savedCounters.SavedCounters
import com.example.counter.settings.Settings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(CounterPage())

        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.counterList -> replaceFragment(SavedCounters())
                R.id.settings -> replaceFragment(Settings())
                R.id.singleCounter -> replaceFragment(CounterPage())
                else ->  replaceFragment(CounterPage())
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment , fragment)
        fragmentTransaction.commit()

    }
}