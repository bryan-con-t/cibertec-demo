package com.example.myapplication.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.InicioActivity
import com.example.myapplication.R

class RecordatorioWorker(context : Context, params : WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val idCanal = "canal_recordatorio"

        // Crear canal de notificación si Android >= Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                idCanal,
                "Recordatorios",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(canal)
        }

        // Intent para abrir InicioActivity mostrando Historial
        val intent = Intent(applicationContext, InicioActivity::class.java)
        intent.putExtra("mostrarFragmento", "historial")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (PendingIntent.FLAG_IMMUTABLE)
        )

        // Crear la notificación
        val builder = NotificationCompat.Builder(applicationContext, idCanal)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Lista de compras")
            .setContentText("¡No olvides revisar tu lista de compras hoy!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Validación de permiso para Android 13+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            || applicationContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1001, builder.build())
        }
        return Result.success()
    }
}
