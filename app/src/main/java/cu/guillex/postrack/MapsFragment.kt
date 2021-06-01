package cu.guillex.postrack

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import cu.guillex.postrack.model.GpsViewModel
import cu.guillex.postrack.model.MqttViewModel
import cu.guillex.postrack.services.ForegroundService

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        myMarker = googleMap.addMarker(MarkerOptions().position(sydney).title("My Marker"))
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        this.googleMaps = googleMap
    }

    //ViewModels
    private lateinit var mqttViewModel: MqttViewModel
    private lateinit var gpsViewModel: GpsViewModel

    private var googleMaps: GoogleMap? = null
    private var myMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        //MQTT
        Log.i("Postrack", "Called ViewModelProvider for MQTT")
        mqttViewModel = ViewModelProvider(this).get(MqttViewModel::class.java)

        mqttViewModel.connectedEvent.observe(this, Observer<Boolean> {
            onConnected()
        })

        mqttViewModel.m_lastData.observe(this, Observer<String> {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })

        //GPS
        Log.i("Postrack", "Called ViewModelProvider for GPS")
        gpsViewModel = ViewModelProvider(this).get(GpsViewModel::class.java)
        gpsViewModel.lastLocation.observe(this, Observer<Location> {
            Log.i("Postrack", it.toString())
            Toast.makeText(activity, "GPS: ${it.latitude}, ${it.longitude}", Toast.LENGTH_LONG).show()

            if(googleMaps != null)
            {
                val myPos = LatLng(it.latitude, it.longitude)
                myMarker!!.position = LatLng(it.latitude, it.longitude)
                googleMaps!!.moveCamera(CameraUpdateFactory.newLatLng(myPos))
            }
        })

        //gpsViewModel.Start()
        gpsViewModel.StartLocationUpdates()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.mqtt_channel)
            val descriptionText = getString(R.string.mqtt_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("MqttChannel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun onConnected(){
        Log.i("Postrack", "onConnected on fragment")

        val serviceIntent = Intent(requireContext(), ForegroundService::class.java)

        requireActivity().startService(serviceIntent)
    }
}