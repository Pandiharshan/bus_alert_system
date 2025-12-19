package com.example.smartdesk

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartdesk.network.NetworkClient
import com.example.smartdesk.sensor.SensorManagerWrapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.max
import kotlin.math.min

data class DashboardState(
    val lux: Float = 0f,
    val low: Float = 50f,
    val high: Float = 200f,
    val alert: Boolean = false,
    val connected: Boolean = false,
    val manual: Boolean = false,
    val history: List<Float> = emptyList()
)

@Serializable
data class SensorMessage(
    val lux: Float = 0f,
    val status: String = "",
    val sensor: String = ""
)

class DashboardViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("dashboard", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    val currentLux = _state.map { it.lux }.stateIn(viewModelScope, SharingStarted.Lazily, 0f)
    val lowThreshold = _state.map { it.low }.stateIn(viewModelScope, SharingStarted.Lazily, 50f)
    val highThreshold = _state.map { it.high }.stateIn(viewModelScope, SharingStarted.Lazily, 200f)
    val isAlert = _state.map { it.alert }.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val isConnected = _state.map { it.connected }.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val luxHistory = _state.map { it.history }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val netClient = NetworkClient(viewModelScope)
    private val sensorWrapper = SensorManagerWrapper(app)

    private var netJob: Job? = null
    private var sensorJob: Job? = null
    private var useFallback = false

    init {
        loadPrefs()
        startMonitoring()
    }

    fun startMonitoring() {
        netJob = viewModelScope.launch {
            netClient.incoming.collect { line ->
                try {
                    val msg = json.decodeFromString<SensorMessage>(line)

                    useFallback = msg.sensor.contains("BH1750_OFF", ignoreCase = true)

                    if (!useFallback) {
                        updateLux(msg.lux)
                        updateState { copy(connected = true) }
                    } else {
                        updateState { copy(connected = false) }
                        startSensorFallback()
                    }
                } catch (e: Exception) {
                    // Ignore parse errors
                }
            }
        }

        viewModelScope.launch {
            netClient.connectAuto()
        }
    }

    fun stopMonitoring() {
        netJob?.cancel()
        sensorJob?.cancel()
        sensorWrapper.stop()
        netClient.disconnect()
    }

    fun toggleConnection() {
        viewModelScope.launch {
            if (_state.value.connected) {
                netClient.disconnect()
                updateState { copy(connected = false) }
                startSensorFallback()
            } else {
                netClient.connectAuto()
            }
        }
    }

    private fun startSensorFallback() {
        if (sensorJob?.isActive == true) return

        sensorJob = viewModelScope.launch {
            sensorWrapper.luxFlow.collect { lux ->
                if (!_state.value.connected || useFallback) {
                    updateLux(lux)
                }
            }
        }
    }

    private fun updateLux(lux: Float) {
        val s = _state.value
        val alert = s.manual || lux < s.low || lux > s.high
        val newHistory = (s.history + lux).takeLast(50)

        updateState {
            copy(lux = lux, alert = alert, history = newHistory)
        }
    }

    fun setLowThreshold(value: Float) {
        val clamped = max(0f, min(value, _state.value.high - 10f))
        updateState { copy(low = clamped) }
        savePrefs()
    }

    fun setHighThreshold(value: Float) {
        val clamped = max(_state.value.low + 10f, min(value, 400f))
        updateState { copy(high = clamped) }
        savePrefs()
    }

    fun toggleManualAlert() {
        updateState { copy(manual = !manual) }
    }

    fun applyPreset(low: Float, high: Float) {
        updateState { copy(low = low, high = high) }
        savePrefs()
    }

    fun sendTimer(seconds: Int) {
        viewModelScope.launch {
            netClient.send("{\"timer\":$seconds}")
        }
    }

    private fun updateState(block: DashboardState.() -> DashboardState) {
        _state.update(block)
    }

    private fun loadPrefs() {
        val low = prefs.getFloat("low", 50f)
        val high = prefs.getFloat("high", 200f)
        updateState { copy(low = low, high = high) }
    }

    private fun savePrefs() {
        val s = _state.value
        prefs.edit()
            .putFloat("low", s.low)
            .putFloat("high", s.high)
            .apply()
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}