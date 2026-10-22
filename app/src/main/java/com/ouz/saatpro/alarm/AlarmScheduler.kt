package com.ouz.saatpro.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ouz.saatpro.data.AlarmPreferences
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val preferences = AlarmPreferences(context)

    companion object {
        const val ALARM_REQUEST_CODE = 1001
        const val ACTION_TRIGGER_ALARM = "com.ouz.saatpro.ACTION_TRIGGER_ALARM"
    }

    fun startRoutine(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        intervalHours: Int
    ) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Eğer seçilen tarih geçmişte kaldıysa bir periyot ekle
        while (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.HOUR_OF_DAY, intervalHours)
        }

        preferences.targetYear = year
        preferences.targetMonth = month
        preferences.targetDay = day
        preferences.targetHour = hour
        preferences.targetMinute = minute
        preferences.intervalHours = intervalHours

        scheduleAtTime(calendar.timeInMillis)
    }

    fun scheduleNextOccurrence() {
        val intervalMillis = preferences.intervalHours * 60 * 60 * 1000L
        val baseTime = if (preferences.nextTriggerMillis > 0L) {
            preferences.nextTriggerMillis
        } else {
            System.currentTimeMillis()
        }
        val nextTime = baseTime + intervalMillis
        scheduleAtTime(nextTime)
    }

    private fun scheduleAtTime(triggerMillis: Long) {
        preferences.isAlarmActive = true
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
