package cu.guillex.postrack.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

//import com.google.android.gms.location.FusedLocationProviderClient

class GpsViewModel(application: Application) : AndroidViewModel(application) {

    private var context : Context?

    var lastLocation = MutableLiveData<Location>()

    //Provides access to location apis
    val fusedLocationClient : FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }

    private var cancellationTokenSource = CancellationTokenSource()

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    init {
        Log.i("Postrack", "GpsViewModel created")

        context = application.applicationContext
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("Postrack", "GpsViewModel destroyed")
    }

    @SuppressLint("MissingPermission")
    fun Start()
    {
        if(context != null)
        {
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                this.cancellationTokenSource.token
            )

            currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                val result = if (task.isSuccessful && task.result != null) {
                    val result: Location = task.result
                    "Location (success): ${result.latitude}, ${result.longitude}"
                }
                else
                {
                    val exception = task.exception
                    "Location (failure): $exception"
                }

                Log.i("Postrack", "location result: $result")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun StartLocationUpdates()
    {
        if(context != null)
        {
            //fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

            locationRequest = LocationRequest()
            locationRequest.interval = 100
            locationRequest.fastestInterval = 50
            locationRequest.smallestDisplacement = 10f
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return

                    if (locationResult.locations.isNotEmpty()) {
                        Log.i("Postrack", "Fused location: $locationResult")
                        lastLocation.value = locationResult.lastLocation
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    fun Stop()
    {
        cancellationTokenSource.cancel()
    }
}