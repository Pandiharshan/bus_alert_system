# Bus Alert System

A mobile application for tracking bus locations and managing student boarding through QR code scanning. The system provides real-time location tracking for drivers and allows students to scan QR codes to board buses.

## Tech Stack

- **Language**: Kotlin
- **Platform**: Android
- **Backend**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Location Services**: Google Play Services Location API
- **Maps**: Google Maps SDK
- **QR Code**: ML Kit Barcode Scanning, ZXing
- **Camera**: CameraX

## Features

### Student Features
- **QR Code Scanning**: Scan bus QR codes to record boarding information
- **Real-time Bus Tracking**: View live bus location on Google Maps
- **Login System**: Basic authentication activity

### Driver Features
- **Location Broadcasting**: Continuous location sharing via foreground service
- **Driver Home Interface**: Basic driver dashboard

### Core Functionality
- **QR Code Generation**: Generate QR codes containing bus information
- **Firebase Integration**: Store boarding records and location data
- **Real-time Updates**: Live location updates using Firestore listeners

## Project Structure

```
app/src/main/java/com/harshan/busalertsystem/
├── models/
│   └── User.kt                    # Basic user data model
├── services/
│   ├── firestore/
│   │   └── FirestoreService.kt    # Firebase service (empty implementation)
│   └── location/
│       ├── LocationService.kt     # Location service (empty implementation)
│       └── DriverLocationService.kt # Foreground service for driver location tracking
├── ui/
│   ├── login/
│   │   └── LoginActivity.kt       # Authentication screen
│   └── student/
│       ├── QrScannerActivity.kt   # QR code scanning with camera
│       ├── StudentHomeActivity.kt # Student dashboard
│       └── StudentMapActivity.kt  # Real-time bus tracking map
└── utils/
    └── QrGenerator.kt             # QR code generation utility

app/src/main/res/
├── drawable/
│   ├── ic_visibility.xml          # Visibility icon
│   └── ic_visibility_off.xml      # Hidden visibility icon
└── layout/
    ├── activity_driver_home.xml   # Driver interface layout (empty)
    ├── activity_login.xml         # Login screen layout (empty)
    ├── activity_qr_scanner.xml    # QR scanner with camera preview
    ├── activity_student_home.xml  # Student dashboard layout (empty)
    └── activity_student_map.xml   # Google Maps fragment
```

## Setup & Installation

### Prerequisites
- Android Studio
- Firebase project with Firestore enabled
- Google Maps API key
- Required permissions in AndroidManifest.xml

### Dependencies Required
Add these dependencies to your `app/build.gradle.kts`:

```kotlin
// CameraX
implementation "androidx.camera:camera-core:1.3.3"
implementation "androidx.camera:camera-camera2:1.3.3"
implementation "androidx.camera:camera-lifecycle:1.3.3"
implementation "androidx.camera:camera-view:1.3.3"

// ML Kit Barcode Scanning
implementation 'com.google.mlkit:barcode-scanning:17.2.0'

// ZXing for QR generation
implementation 'com.google.zxing:core:3.5.1'

// Location Services
implementation "com.google.android.gms:play-services-location:21.2.0"

// Firebase
implementation platform("com.google.firebase:firebase-bom:33.1.0")
implementation "com.google.firebase:firebase-firestore"
implementation "com.google.firebase:firebase-auth"

// Google Maps
implementation "com.google.android.gms:play-services-maps:18.2.0"
```

### Permissions Required
Add to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### Installation Steps
1. Clone the repository
2. Open in Android Studio
3. Add required dependencies to build.gradle
4. Configure Firebase project and add google-services.json
5. Add Google Maps API key
6. Register DriverLocationService in AndroidManifest.xml
7. Build and run the project

## How to Run the Project

1. **Driver Mode**: Start the DriverLocationService to begin broadcasting location
2. **Student Mode**: 
   - Use QrScannerActivity to scan bus QR codes
   - Use StudentMapActivity to view real-time bus location
3. **QR Code Generation**: Use QrGenerator.generateQrCode() to create bus QR codes

## Data Structure

### QR Code Format
```json
{"busId": "bus_1"}
```

### Firestore Collections
- `buses/{busId}`: Bus location data (lat, lng, speed, lastSeen)
- `boarding/{date}/{busId}/{studentId}`: Boarding records

## Use Cases

- **Educational Institutions**: Track school buses and manage student boarding
- **Public Transportation**: Monitor bus locations and passenger boarding
- **Corporate Shuttles**: Manage employee transportation

## Limitations

- **Incomplete UI**: Most activity layouts are empty and need implementation
- **Missing Build Configuration**: No build.gradle or AndroidManifest.xml files present
- **Empty Service Classes**: FirestoreService and LocationService have no implementation
- **No Error Handling**: Limited error handling in location and scanning services
- **Single Bus Support**: Currently hardcoded to support only "bus_1"
- **No User Management**: Basic user model without proper authentication flow
- **Missing Navigation**: No navigation between activities implemented

## Future Improvements

- Implement complete UI for all activities
- Add proper user authentication and role management
- Support multiple buses with dynamic bus ID assignment
- Add offline capability and data synchronization
- Implement push notifications for bus arrival alerts
- Add route planning and estimated arrival times
- Create admin panel for bus and route management
- Add comprehensive error handling and user feedback

## Author

Harshan - Bus Alert System