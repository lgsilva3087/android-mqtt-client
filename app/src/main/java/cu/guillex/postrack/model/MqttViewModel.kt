package cu.guillex.postrack.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MqttViewModel : ViewModel() {
    var example = MutableLiveData<Int>()

    init {
        Log.i("MqttViewModel", "MqttViewModel created")

        example.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MqttViewModel", "MqttViewModel destroyed")
    }
}