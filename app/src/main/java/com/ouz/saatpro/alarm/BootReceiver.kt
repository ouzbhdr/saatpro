package com.ouz.saatpro.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ouz.saatpro.data.AlarmPreferences
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.LOCKED_BOOT_COMPLETED"
        ) {
            val prefs = AlarmPreferences(context)
            if (prefs.isAlarmActive) {
                val scheduler = AlarmScheduler(context)
                if (prefs.nextTriggerMillis <= System.currentTimeMillis()) {
                    scheduler.scheduleNextOccurrence()
                } else {
                    val cal = Calendar.getInstance()
                    val year = if (prefs.targetYear != -1) prefs.targetYear else cal.get(Calendar.YEAR)
                    val month = if (prefs.targetMonth != -1) prefs.targetMonth else cal.get(Calendar.MONTH)
                    val day = if (prefs.targetDay != -1) prefs.targetDay else cal.get(Calendar.DAY_OF_MONTH)

                    scheduler.startRoutine(
                        year = year,
                        month = month,
                        day = day,
                        hour = prefs.targetHour,
                        minute = prefs.targetMinute,
                        intervalHours = prefs.intervalHours,
                        intervalMinutes = prefs.intervalMinutes
                    )
                }
            }
        }
    }
}
