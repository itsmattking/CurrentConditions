package me.mking.currentconditions.data.providers

interface DateTimeProvider {
    fun nowInEpochSeconds(): Long
}