package com.example.smartdesk.network

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.min

class NetworkClient(private val scope: CoroutineScope) {

    private val _incoming = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 64)
    val incoming: SharedFlow<String> = _incoming.asSharedFlow()

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var readJob: Job? = null

    private var isActive = false
    private var retryDelay = 1000L

    suspend fun connectAuto() = withContext(Dispatchers.IO) {
        disconnect()
        isActive = true

        val subnets = listOf("192.168.43", "192.168.4")
        val port = 3333

        for (subnet in subnets) {
            for (lastOctet in 1..254) {
                if (!isActive) return@withContext

                val ip = "$subnet.$lastOctet"
                try {
                    val s = Socket()
                    s.connect(InetSocketAddress(ip, port), 500)

                    socket = s
                    writer = PrintWriter(s.getOutputStream(), true)
                    reader = BufferedReader(InputStreamReader(s.getInputStream()))

                    retryDelay = 1000L
                    startReading()
                    return@withContext

                } catch (e: Exception) {
                    // Continue scanning
                }
            }
        }

        // No device found, retry with backoff
        if (isActive) {
            delay(retryDelay)
            retryDelay = min(retryDelay * 2, 30000L)
            connectAuto()
        }
    }

    suspend fun send(msg: String) = withContext(Dispatchers.IO) {
        try {
            writer?.println(msg)
        } catch (e: Exception) {
            reconnect()
        }
    }

    fun disconnect() {
        isActive = false
        readJob?.cancel()
        readJob = null

        try {
            writer?.close()
            reader?.close()
            socket?.close()
        } catch (e: Exception) {
            // Ignore
        }

        writer = null
        reader = null
        socket = null
    }

    private fun startReading() {
        readJob = scope.launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    val line = reader?.readLine() ?: break
                    _incoming.emit(line)
                }
            } catch (e: Exception) {
                if (isActive) reconnect()
            }
        }
    }

    private fun reconnect() {
        scope.launch {
            disconnect()
            delay(retryDelay)
            retryDelay = min(retryDelay * 2, 30000L)
            connectAuto()
        }
    }
}