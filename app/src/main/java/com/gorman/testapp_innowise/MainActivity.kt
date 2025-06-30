package com.gorman.testapp_innowise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gorman.testapp_innowise.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.gorman.testapp_innowise.ui.home.HomeViewModel
import com.gorman.testapp_innowise.ui.home.LoadResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var indicator: View
    private val homeViewModel: HomeViewModel by viewModels()
    private val isLoading = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            isLoading.get()
        }
        lifecycleScope.launch {
            homeViewModel.loadResult.collect { loadResult ->
                when (loadResult) {
                    is LoadResult.Loading -> isLoading.set(true)
                    is LoadResult.Success,
                    is LoadResult.Empty,
                    is LoadResult.Error -> isLoading.set(false)
                }
            }
        }
        homeViewModel.loadPhotos("nature")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNav = binding.navView
        indicator = binding.indicator

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_bookmarks
            )
        )
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = true

        binding.navView.setupWithNavController(navController)

        bottomNav.post {
            moveIndicatorToIndex(0)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val index = when(destination.id) {
                R.id.navigation_home -> 0
                R.id.navigation_bookmarks -> 1
                else -> 0
            }
            moveIndicatorToIndex(index)
        }
    }

    private fun moveIndicatorToIndex(index: Int) {
        val menuView = bottomNav.getChildAt(0) as ViewGroup
        val itemView = menuView.getChildAt(index) ?: return

        val container = indicator.parent as ViewGroup

        val itemLocation = IntArray(2)
        val containerLocation = IntArray(2)

        itemView.getLocationInWindow(itemLocation)
        container.getLocationInWindow(containerLocation)

        val itemCenterX = itemLocation[0] + itemView.width / 2
        val containerLeft = containerLocation[0]

        val targetX = itemCenterX - containerLeft - indicator.width / 2 - 3

        indicator.animate()
            .translationX(targetX.toFloat())
            .setDuration(200)
            .start()
    }
}