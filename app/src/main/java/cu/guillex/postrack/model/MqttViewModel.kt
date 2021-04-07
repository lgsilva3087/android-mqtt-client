package cu.guillex.postrack.model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttViewModel(application: Application) : AndroidViewModel(application) {
    var example = MutableLiveData<Int>()

    private lateinit var mqttAndroidClient : MqttAndroidClient

    var m_lastData = MutableLiveData<String>("")

    init {
        Log.i("MqttViewModel", "MqttViewModel created")

        example.value = 0

        connect(application.applicationContext)
        receiveMessages()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MqttViewModel", "MqttViewModel destroyed")
    }

    fun connect(applicationContext : Context)
    {
        val connectionOptions : MqttConnectOptions = MqttConnectOptions()
        connectionOptions.userName = "vertex"
        val password : String = "Vertex2020*"
        connectionOptions.password = password.toCharArray()

        mqttAndroidClient = MqttAndroidClient (applicationContext,
            "tcp://mqtt.prod.uci.cu:1883",
            "lgsilva3087" )

        try {
            val token = mqttAndroidClient.connect(connectionOptions)

            token.actionCallback = object : IMqttActionListener
            {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i("MqttViewModel", "success ")
                    //connectionStatus = true
                    // Give your callback on connection established here
                    subscribe("#")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //connectionStatus = false
                    Log.i("MqttViewModel", "failure")

                    // Give your callback on connection failure here
                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String) {
        val qos = 2 // Mention your qos value

        try {
            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on Subscription here
                    Log.i("MqttViewModel", "Subscrito a $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Give your subscription failure callback here
                    Log.i("MqttViewModel", "No Subscrito a $topic")
                }
            })
        } catch (e: MqttException) {
            // Give your subscription failure callback here
            Log.i("MqttViewModel", "Excepcion subscribiendo a $topic")
        }
    }

    fun unSubscribe(topic: String) {
        try {
            val unsubToken = mqttAndroidClient.unsubscribe(topic)

            unsubToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on unsubscribing here
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Give your callback on failure here
                }
            }
        } catch (e: MqttException) {
            // Give your callback on failure here
        }
    }

    fun receiveMessages() {
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                //connectionStatus = false
                // Give your callback on failure here
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.i("MqttViewModel", "Message arrived on topic: $topic")
                try {
                    val data = String(message.payload, charset("UTF-8"))

                    onMessageReceived(data)
                    // data is the desired received message
                    // Give your callback on message received here
                } catch (e: Exception) {
                    // Give your callback on error here
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
            }
        })
    }

    private fun onMessageReceived(data: String) {
        m_lastData.value = data

        Log.i("MqttViewModel", "Message received: $data")
    }

    fun publish(topic: String, data: String) {
        val encodedPayload : ByteArray

        try {
            encodedPayload = data.toByteArray(charset("UTF-8"))
            val message = MqttMessage(encodedPayload)
            message.qos = 2
            message.isRetained = false
            mqttAndroidClient.publish(topic, message)
        } catch (e: Exception) {
            // Give Callback on error here
        } catch (e: MqttException) {
            // Give Callback on error here
        }
    }

    fun disconnect() {
        try {
            val disconToken = mqttAndroidClient.disconnect()

            disconToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    //connectionStatus = false
                    // Give Callback on disconnection here
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Give Callback on error here
                }
            }
        } catch (e: MqttException) {
            // Give Callback on error here
        }
    }
}