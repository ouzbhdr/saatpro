package com.ouz.saatpro.data

import android.content.Context
import android.content.SharedPreferences

class AlarmPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "saat_pro_preferences"
        private const val KEY_IS_ACTIVE = "key_is_alarm_active"
        private const val KEY_TARGET_YEAR = "key_target_year"
        private const val KEY_TARGET_MONTH = "key_target_month"
        private const val KEY_TARGET_DAY = "key_target_day"
        private const val KEY_TARGET_HOUR = "key_target_hour"
        private const val KEY_TARGET_MINUTE = "key_target_minute"
        private const val KEY_INTERVAL_HOURS = "key_interval_hours"
        private const val KEY_INTERVAL_MINUTES = "key_interval_minutes"
        private const val KEY_NEXT_TRIGGER_MILLIS = "key_next_trigger_millis"
        private const val KEY_RINGTONE_URI = "key_ringtone_uri"
        private const val KEY_RINGTONE_TITLE = "key_ringtone_title"
    }

    var isAlarmActive: Boolean
        get() = prefs.getBoolean(KEY_IS_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ACTIVE, value).apply()

    var targetYear: Int
        get() = prefs.getInt(KEY_TARGET_YEAR, -1)
        set(value) = prefs.edit().putInt(KEY_TARGET_YEAR, value).apply()

    var targetMonth: Int
        get() = prefs.getInt(KEY_TARGET_MONTH, -1)
        set(value) = prefs.edit().putInt(KEY_TARGET_MONTH, value).apply()

    var targetDay: Int
        get() = prefs.getInt(KEY_TARGET_DAY, -1)
        set(value) = prefs.edit().putInt(KEY_TARGET_DAY, value).apply()

    var targetHour: Int
        get() = prefs.getInt(KEY_TARGET_HOUR, 12)
        set(value) = prefs.edit().putInt(KEY_TARGET_HOUR, value).apply()

    var targetMinute: Int
        get() = prefs.getInt(KEY_TARGET_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_TARGET_MINUTE, value).apply()

    var intervalHours: Int
        get() = prefs.getInt(KEY_INTERVAL_HOURS, 48)
        set(value) = prefs.edit().putInt(KEY_INTERVAL_HOURS, value).apply()

    var intervalMinutes: Int
        get() = prefs.getInt(KEY_INTERVAL_MINUTES, 0)
        set(value) = prefs.edit().putInt(KEY_INTERVAL_MINUTES, value).apply()

    var nextTriggerMillis: Long
        get() = prefs.getLong(KEY_NEXT_TRIGGER_MILLIS, 0L)
        set(value) = prefs.edit().putLong(KEY_NEXT_TRIGGER_MILLIS, value).apply()

    var ringtoneUri: String
        get() = prefs.getString(KEY_RINGTONE_URI, "") ?: ""
        set(value) = prefs.edit().putString(KEY_RINGTONE_URI, value).apply()

    var ringtoneTitle: String
        get() = prefs.getString(KEY_RINGTONE_TITLE, "Varsayılan Alarm") ?: "Varsayılan Alarm"
        set(value) = prefs.edit().putString(KEY_RINGTONE_TITLE, value).apply()
}
