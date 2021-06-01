package cu.guillex.postrack

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_CONTACTS
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import cu.guillex.postrack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        requestLocationPermissions()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.redFragmentMenuItem -> {
                print("Show Red Fragment")
                val id = R.id.nav_host_fragment
                val navHostFragment = supportFragmentManager.findFragmentById(id) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.redFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(99)
    fun requestLocationPermissions() {
        if (EasyPermissions.hasPermissions(this, ACCESS_FINE_LOCATION)) {
            Log.i("Postrack", "Location permission granted")
        } else {

            Toast.makeText(applicationContext, "No location permission", Toast.LENGTH_LONG)

            EasyPermissions.requestPermissions(
                this,
                getString(R.string.location_rationale),
                99,
                ACCESS_FINE_LOCATION, READ_CONTACTS
            )
        }
    }
}