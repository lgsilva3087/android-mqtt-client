package cu.guillex.postrack

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cu.guillex.postrack.model.MqttViewModel
import cu.guillex.postrack.services.ForegroundService

class MqttConnectionFragment : Fragment() {
    private lateinit var mqttViewModel: MqttViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        Log.i("MqttConnectionFragment", "Called ViewModelProvider")
        mqttViewModel = ViewModelProvider(this).get(MqttViewModel::class.java)

        mqttViewModel.connectedEvent.observe(this, Observer<Boolean> {
            onConnected()
        })

        mqttViewModel.m_lastData.observe(this, Observer<String> {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mqtt_connection, container, false)
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
        val serviceIntent = Intent(requireContext(), ForegroundService::class.java)

        requireActivity().startService(serviceIntent)
    }
}