package com.pavel.scopictask.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.pavel.scopictask.ui.auth.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pavel.scopictask.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.signInFragment || destination.id == R.id.signUpFragment) {
                if(!viewModel.userAuthenticatedStatus) {
                    navController.popBackStack(R.id.listItemFragment, true)
                }
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

}