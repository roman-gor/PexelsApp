package com.gorman.testapp_innowise

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.gorman.testapp_innowise.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.gorman.testapp_innowise.ui.home.HomeViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.core.app.ActivityCompat
import com.gorman.testapp_innowise.ui.LoadResult
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val isLoading = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            isLoading.get()
        }
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSplashScreenLoading()
        requestLegacyPermissionsIfNeeded()
        setupNavigation()
    }

    private fun initSplashScreenLoading()
    {
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
    }

    private fun requestLegacyPermissionsIfNeeded()
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
            }
        }
    }

    private fun setupNavigation()
    {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars =
            nightModeFlags != Configuration.UI_MODE_NIGHT_YES
        binding.navView.setupWithNavController(navController)

        binding.navView.post {
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
        val bottomNav = binding.navView
        val indicator = binding.indicator
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}