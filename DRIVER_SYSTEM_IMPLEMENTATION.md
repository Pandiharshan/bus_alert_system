# Driver System Implementation Guide

## Overview
This document explains the complete Firebase-based driver authentication and management system for Campus Bus Buddy.

## Architecture

### Firebase Collections Structure

```
drivers/{driverUID}
├── name: String
├── email: String
├── phone: String
├── photoUrl: String
├── assignedBusId: String
├── isActive: Boolean
└── createdAt: Long

buses/{busId}
├── busNumber: Int
├── capacity: Int
├── activeDriverId: String
├── activeDriverName: String
└── activeDriverPhone: String
```

## Implementation Flow

### 1. Admin Creates Driver Account

**Function:** `FirebaseManager.createDriverAccount()`

**Steps:**
1. Admin provides driver details (name, email, phone, assignedBusId)
2. System creates Firebase Auth account with email/password
3. Optional: Upload driver photo to Firebase Storage
4. Create driver document in Firestore using Auth UID as document ID
5. Sign out the newly created driver account

**Code Example:**
```kotlin
val driverData = DriverData(
    name = "John Doe",
    email = "john.driver@example.com",
    phone = "+1234567890",
    assignedBusId = "bus_01"
)

when (val result = FirebaseManager.createDriverAccount(driverData, "tempPassword123", photoUri)) {
    is DriverResult.Success -> {
        // Driver created successfully
        println("Driver UID: ${result.driverUid}")
    }
    is DriverResult.Error -> {
        // Handle error
        println("Error: ${result.message}")
    }
}
```

### 2. Driver Authentication

**Function:** `FirebaseManager.authenticateDriver()`

**Steps:**
1. Driver enters email and password on DriverAuthenticationScreen
2. System authenticates with Firebase Auth
3. Fetch driver document from Firestore using Auth UID
4. Fetch assigned bus information
5. Return driver and bus info to UI

**Code Example:**
```kotlin
when (val result = FirebaseManager.authenticateDriver(email, password)) {
    is DriverAuthResult.Success -> {
        val driverInfo = result.driverInfo
        val busInfo = result.busInfo
        // Navigate to Driver Home
    }
    is DriverAuthResult.Error -> {
        // Show error message
        errorMessage = result.message
    }
}
```

### 3. Driver Home Page Data Binding

**Screen:** `DriverHomeScreen`

**Features:**
- Displays driver name from Firestore
- Shows driver photo (loaded via Coil)
- Displays assigned bus number
- Shows employee ID (first 8 characters of UID)

**Data Loading:**
```kotlin
LaunchedEffect(Unit) {
    driverInfo = FirebaseManager.getCurrentDriverInfo()
    if (driverInfo?.assignedBusId?.isNotEmpty() == true) {
        busInfo = FirebaseManager.getBusInfo(driverInfo!!.assignedBusId)
    }
}
```

### 4. Exclusive Bus Lock System

**Function:** `FirebaseManager.activateDriverAndLockBus()`

**Logic:**
1. Check if bus has an active driver (`activeDriverId` field)
2. If empty → Assign current driver to bus
3. If filled with another driver → Block access with error message
4. If filled with same driver → Allow access (re-login scenario)
5. Update driver status to `isActive = true`

**Code Example:**
```kotlin
when (val result = FirebaseManager.activateDriverAndLockBus(driverInfo)) {
    is BusLockResult.Success -> {
        // Bus activated successfully
    }
    is BusLockResult.Error -> {
        // Bus is locked by another driver
        showError(result.message)
    }
}
```

### 5. Deactivate Driver and Unlock Bus

**Function:** `FirebaseManager.deactivateDriverAndUnlockBus()`

**Steps:**
1. Clear bus active driver fields
2. Set driver `isActive = false`
3. Allow other drivers to access the bus

## Data Models

### DriverData (for creation)
```kotlin
data class DriverData(
    val name: String,
    val email: String,
    val phone: String,
    val assignedBusId: String
)
```

### DriverInfo (runtime)
```kotlin
data class DriverInfo(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val photoUrl: String,
    val assignedBusId: String,
    val isActive: Boolean
)
```

### BusInfo
```kotlin
data class BusInfo(
    val busId: String,
    val busNumber: Int,
    val capacity: Int,
    val activeDriverId: String,
    val activeDriverName: String,
    val activeDriverPhone: String
)
```

## Security Rules

### Firestore Security Rules (Recommended)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Drivers collection
    match /drivers/{driverUid} {
      // Drivers can read their own document
      allow read: if request.auth != null && request.auth.uid == driverUid;
      
      // Only admins can create/update driver documents
      allow write: if request.auth != null && 
                      exists(/databases/$(database)/documents/admins/$(request.auth.token.email));
    }
    
    // Buses collection
    match /buses/{busId} {
      // Drivers can read bus info
      allow read: if request.auth != null;
      
      // Drivers can update their assigned bus
      allow update: if request.auth != null && 
                       get(/databases/$(database)/documents/drivers/$(request.auth.uid)).data.assignedBusId == busId;
      
      // Only admins can create buses
      allow create: if request.auth != null && 
                       exists(/databases/$(database)/documents/admins/$(request.auth.token.email));
    }
    
    // Admins collection
    match /admins/{email} {
      allow read: if request.auth != null && request.auth.token.email == email;
      allow write: if false; // Only via Firebase Console
    }
  }
}
```

## Testing the Driver Flow

### Test Data Setup

1. **Create a test bus in Firestore:**
```json
buses/bus_01
{
  "busNumber": 15,
  "capacity": 50,
  "activeDriverId": "",
  "activeDriverName": "",
  "activeDriverPhone": ""
}
```

2. **Admin creates driver account:**
```kotlin
val testDriver = DriverData(
    name = "Test Driver",
    email = "testdriver@campusbuddy.com",
    phone = "+1234567890",
    assignedBusId = "bus_01"
)
FirebaseManager.createDriverAccount(testDriver, "TestPass123")
```

3. **Driver logs in:**
- Email: testdriver@campusbuddy.com
- Password: TestPass123

4. **Verify driver home page shows:**
- Driver name: "Test Driver"
- Bus number: "15"
- Employee ID: (first 8 chars of UID)

## Error Handling

### Common Errors and Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| "Driver profile not found" | Auth account exists but no Firestore document | Ensure driver document is created with Auth UID |
| "Bus is currently active with [name]" | Another driver has locked the bus | Wait for other driver to finish or admin intervention |
| "No bus assigned to this driver" | assignedBusId is empty | Admin must assign a bus to the driver |
| "Assigned bus not found" | Bus document doesn't exist | Create bus document in Firestore |

## Future Enhancements

1. **Password Reset Flow**
   - Implement Firebase password reset email
   - Add "Forgot Password" functionality

2. **Driver Photo Upload**
   - Allow drivers to update their profile photo
   - Implement photo compression before upload

3. **Real-time Bus Status**
   - Use Firestore listeners for live bus status updates
   - Show active drivers on admin dashboard

4. **Trip History**
   - Log driver trips in separate collection
   - Track start/end times and locations

## Dependencies Added

```kotlin
// Firebase Storage for photo uploads
implementation("com.google.firebase:firebase-storage")

// Coil for image loading
implementation("io.coil-kt:coil-compose:2.5.0")
```

## Notes

- Auth UID is used as document ID for consistency
- No passwords are stored in Firestore (only in Firebase Auth)
- Bus lock system prevents concurrent driver access
- Driver status (`isActive`) tracks current shift status
- System is designed for easy extension to student flow
