package me.mking.currentconditions.data.providers

interface NetworkStatusProvider {
    fun isConnected(): Boolean
}