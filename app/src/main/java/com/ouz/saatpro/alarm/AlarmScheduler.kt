package com.ouz.saatpro.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ouz.saatpro.AlarmActivity
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
        intervalHours: Int,
        intervalMinutes: Int
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

        val totalIntervalMinutes = (intervalHours * 60) + intervalMinutes
        val stepMinutes = if (totalIntervalMinutes > 0) totalIntervalMinutes else 48 * 60

        while (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.MINUTE, stepMinutes)
        }

        preferences.targetYear = year
        preferences.targetMonth = month
        preferences.targetDay = day
        preferences.targetHour = hour
        preferences.targetMinute = minute
        preferences.intervalHours = intervalHours
        preferences.intervalMinutes = intervalMinutes

        scheduleAtTime(calendar.timeInMillis)
    }

    fun scheduleNextOccurrence() {
        val totalIntervalMillis = ((preferences.intervalHours * 60L) + preferences.intervalMinutes) * 60L * 1000L
        val baseTime = if (preferences.nextTriggerMillis > 0L) {
            preferences.nextTriggerMillis
        } else {
            System.currentTimeMillis()
        }
        val nextTime = baseTime + totalIntervalMillis
        scheduleAtTime(nextTime)
    }

    private fun scheduleAtTime(triggerMillis: Long) {
        preferences.isAlarmActive = true
        preferences.nextTriggerMillis = triggerMillis

        // 1. Alarm çaldığında doğrudan tetiklenecek Receiver intenti
        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        val operationIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Sistem kilit ekranındayken gösterilecek tam ekran intenti
        val showIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            ALARM_REQUEST_CODE + 1,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Android'in yerel alarm mimarisi: AlarmClockInfo tüm arka plan engellerini deler
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerMillis, showPendingIntent)
        alarmManager.setAlarmClock(alarmClockInfo, operationIntent)
    }

    fun cancelRoutine() {
        preferences.isAlarmActive = false
        preferences.nextTriggerMillis = 0L

        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        val operationIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(operationIntent)
    }
}
