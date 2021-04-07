package cu.guillex.postrack

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

class MqttConnectionFragment : Fragment() {
    private lateinit var mqttViewModel: MqttViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("MqttConnectionFragment", "Called ViewModelProvider")
        mqttViewModel = ViewModelProvider(this).get(MqttViewModel::class.java)

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
}