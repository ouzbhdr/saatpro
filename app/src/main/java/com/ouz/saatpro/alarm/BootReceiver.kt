package com.ouz.saatpro.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ouz.saatpro.data.AlarmPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.LOCKED_BOOT_COMPLETED"
        ) {
            val prefs = AlarmPreferences(context)
            if (prefs.isAlarmActive) {
                val scheduler = AlarmScheduler(context)
                // Cihaz kapalı kaldığı sürede alarm vakti geçtiyse bir sonraki seansı kurar
                if (prefs.nextTriggerMillis <= System.currentTimeMillis()) {
                    scheduler.scheduleNextOccurrence()
                } else {
                    scheduler.startRoutine(
                        hour = prefs.targetHour,
                        minute = prefs.targetMinute,
                        intervalHours = prefs.intervalHours
                    )
                }
            }
        }
    }
}
