package me.mking.currentconditions.data.providers

import java.util.*
import javax.inject.Inject

class CalendarDateTimeProvider @Inject constructor() : DateTimeProvider {
    override fun nowInEpochSeconds(): Long = Calendar.getInstance().timeInMillis / 1000
}