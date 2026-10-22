package com.ouz.saatpro.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.ouz.saatpro.data.AlarmPreferences
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val preferences = AlarmPreferences(context)

    companion object {
        const val ALARM_REQUEST_CODE = 1001
        const val ACTION_TRIGGER_ALARM = "com.ouz.saatpro.ACTION_TRIGGER_ALARM"
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * Döngüyü belirlenen saat ve dakikadan itibaren başlatır.
     */
    fun startRoutine(hour: Int, minute: Int, intervalHours: Int = 48) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Eğer seçilen saat geçmişse bir sonraki periyota at
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.HOUR_OF_DAY, intervalHours)
        }

        scheduleAtTime(calendar.timeInMillis, hour, minute, intervalHours)
    }

    /**
     * Alarm çaldığında veya durdurulduğunda bir sonraki döngüyü kesintisiz kurar.
     */
    fun scheduleNextOccurrence() {
        val intervalMillis = preferences.intervalHours * 60 * 60 * 1000L
        val baseTime = if (preferences.nextTriggerMillis > 0L) {
            preferences.nextTriggerMillis
        } else {
            System.currentTimeMillis()
        }
        val nextTime = baseTime + intervalMillis

        scheduleAtTime(
            triggerMillis = nextTime,
            hour = preferences.targetHour,
            minute = preferences.targetMinute,
            intervalHours = preferences.intervalHours
        )
    }

    private fun scheduleAtTime(triggerMillis: Long, hour: Int, minute: Int, intervalHours: Int) {
        preferences.isAlarmActive = true
        preferences.targetHour = hour
        preferences.targetMinute = minute
        preferences.intervalHours = intervalHours
        preferences.nextTriggerMillis = triggerMillis

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Doze modunu delen ve kilit ekranını uyandıran en kritik çağrı
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent
        )
    }

    fun cancelRoutine() {
        preferences.isAlarmActive = false
        preferences.nextTriggerMillis = 0L

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
