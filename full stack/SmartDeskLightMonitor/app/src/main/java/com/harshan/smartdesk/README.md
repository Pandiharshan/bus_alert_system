# SmartDesk Light Monitor

An Android app that monitors ambient light levels from a NodeMCU ESP8266 device and displays real-time data using Jetpack Compose. Falls back to the phone's built-in light sensor if the external device is unavailable.

## Features

- Real-time light intensity monitoring via TCP socket connection
- Blynk cloud integration for remote notifications
- Material Design 3 UI with Jetpack Compose
- Automatic fallback to phone's ambient light sensor
- Background service for continuous monitoring

## Prerequisites

- *Android Studio* Hedgehog (2023.1.1) or later
- *Android device* running Android 8.0 (API 26) or higher
- *NodeMCU ESP8266* configured as TCP server on port 3333
- *Phone hotspot* for local network connectivity
- *Blynk account* and auth token (for cloud notifications)

## Setup

### 1. Configure Blynk Auth Token

Edit the auth token in your code:

*File:* app/src/main/java/com/example/smartdesk/BlynkService.kt (or relevant service file)
kotlin
private const val BLYNK_AUTH_TOKEN = "YOUR_BLYNK_AUTH_TOKEN_HERE"


Replace YOUR_BLYNK_AUTH_TOKEN_HERE with your actual Blynk authentication token from the Blynk Console.

### 2. Build & Install

#### Using Android Studio:
1. Open the project in Android Studio
2. Connect your Android device via USB (enable USB debugging)
3. Click *Run* (▶) or press Shift+F10

#### Using Command Line:
bash
# Build debug APK
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug

# Or manually install
adb install app/build/outputs/apk/debug/app-debug.apk


## Testing with NodeMCU

1. *Configure NodeMCU:* Flash your ESP8266 with TCP server code listening on port *3333*
2. *Start Phone Hotspot:*
    - Hotspot name: AndroidAP (or your preferred name)
    - Ensure NodeMCU connects to this hotspot
3. *Launch App:* Open SmartDesk Light Monitor
4. *Verify Connection:* App will display "Connected" status and show light readings
5. *Test Fallback:* Disable hotspot or NodeMCU to verify phone sensor fallback

*Expected NodeMCU IP:* Typically 192.168.43.xxx (Android hotspot subnet)  
*Port:* 3333

## Fallback Mode

If the NodeMCU connection fails, the app automatically switches to the phone's built-in ambient light sensor. A notification indicates the active sensor source.

## Permissions

The app requires:
- INTERNET - TCP socket communication
- FOREGROUND_SERVICE - Background monitoring
- POST_NOTIFICATIONS - Light level alerts (Android 13+)

## Changelog

### v1.0 (Initial Release)
- Real-time light monitoring from NodeMCU
- Blynk cloud integration
- Phone sensor fallback
- Material 3 UI with dark theme support
- Background service with persistent notification

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

---

*Note:* Ensure your NodeMCU firmware sends light sensor data in the expected format over the TCP connection.