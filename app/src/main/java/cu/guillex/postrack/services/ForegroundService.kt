package cu.guillex.postrack.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import cu.guillex.postrack.MainActivity
import cu.guillex.postrack.R

class ForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("Mqtt", "ForegroundService - OnCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i("MQTT", "ForegroundService - onStartCommand")

        if (intent != null && intent.getBooleanExtra("Close_Action_Intent", false)) {
            Log.i("MQTT", "Cerrar intent")
            stopSelf()
            return START_NOT_STICKY
        }

        val pendingIntent: PendingIntent =
                Intent(this, MainActivity::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(this, 0, notificationIntent, 0)
                }

        val serviceActionIntent : PendingIntent =
                Intent(this, ForegroundService::class.java).let {
                    it.putExtra("Close_Action_Intent", true)
                    PendingIntent.getService(this, 0, it, 0)
                }

        val builder = NotificationCompat.Builder(this, "MqttChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Connection success")
                .setContentText("Conectado al servidor MQTT.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)    //Este Intent es el que levanta el activity cuando se toca la notificación
                .addAction(R.drawable.ic_launcher_foreground, "Close", serviceActionIntent)
                //.setAutoCancel(true)              //automatically removes the notification when the user taps it

        startForeground(1, builder.build())

        return START_NOT_STICKY
    }
}