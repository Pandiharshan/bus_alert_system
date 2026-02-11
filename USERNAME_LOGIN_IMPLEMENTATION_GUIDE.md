# Username Login System - Complete Implementation Guide

## CRITICAL CHANGES COMPLETED

### ✅ Data Classes Updated
- Added `username` field to: AdminInfo, DriverData, DriverInfo, StudentData, StudentInfo
- Email remains for Firebase Auth (backend only)

## REMAINING CHANGES NEEDED

### 1. FirebaseManager Authentication Functions

#### Update `authenticateAdmin()`
```kotlin
suspend fun authenticateAdmin(username: String, password: String): AuthResult {
    // Query admins collection for username
    val adminQuery = firestore.collection("admins")
        .whereEqualTo("username", username)
        .get()
        .await()
    
    if (adminQuery.isEmpty) {
        return AuthResult.Error("Invalid username or password")
    }
    
    val email = adminQuery.documents.first().getString("email") ?: ""
    
    // Use email for Firebase Auth
    val result = auth.signInWithEmailAndPassword(email, password).await()
    // ... rest of logic
}
```

#### Update `authenticateDriver()` - Similar pattern
#### Update `authenticateStudent()` - Similar pattern

### 2. Create Driver Account - Add Username
```kotlin
suspend fun createDriverAccount(
    driverData: DriverData,  // Now includes username
    password: String,
    photoUri: Uri?
): DriverResult {
    // Create auth account with email
    val authResult = auth.createUserWithEmailAndPassword(driverData.email, password).await()
    
    // Store username in Firestore
    val driverDoc = hashMapOf(
        "name" to driverData.name,
        "username" to driverData.username,  // NEW
        "email" to driverData.email,
        // ... rest
    )
}
```

### 3. Update Login Screens

#### AdminLoginScreen.kt
- Change label: "Admin Email" → "Username"
- Change placeholder: "Enter admin email" → "Enter username"
- Remove email keyboard type
- Pass username to authenticateAdmin()

#### DriverAuthenticationScreen.kt  
- Change label: "Employee ID" → "Username"
- Pass username to authenticateDriver()

#### StudentLoginScreen.kt
- Change label: "Email" → "Username"  
- Change placeholder: "Enter your email" → "Enter username"
- Remove email keyboard type
- Pass username to authenticateStudent()

### 4. Add User Screens - Add Username Field

#### AddDriverScreen.kt
```kotlin
var username by remember { mutableStateOf("") }

// Add username field before email
FormField(
    label = "Username",
    value = username,
    onValueChange = { username = it },
    placeholder = "Enter unique username",
    icon = R.drawable.ic_person
)

// Update DriverData creation
val driverData = DriverData(
    name = name.trim(),
    username = username.trim(),  // NEW
    email = email.trim(),
    phone = phone.trim(),
    assignedBusId = assignedBusId.trim()
)
```

#### AddStudentScreen.kt - Similar changes

### 5. Database Screens - Add Edit Username/Password

#### DriverDatabaseScreen.kt - EditDriverDialog
```kotlin
@Composable
private fun EditDriverDialog(
    driver: DriverInfo,
    onDismiss: () -> Unit,
    onSave: (DriverInfo, String?) -> Unit  // Add optional new password
) {
    var username by remember { mutableStateOf(driver.username) }
    var newPassword by remember { mutableStateOf("") }
    var showPasswordField by remember { mutableStateOf(false) }
    
    // Add username field
    OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text("Username") }
    )
    
    // Add password change option
    TextButton(onClick = { showPasswordField = !showPasswordField }) {
        Text("Change Password")
    }
    
    if (showPasswordField) {
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation()
        )
    }
    
    // Save button
    Button(onClick = {
        val updatedDriver = driver.copy(username = username.trim())
        onSave(updatedDriver, newPassword.ifBlank { null })
    })
}
```

#### Update FirebaseManager.updateDriverInfo()
```kotlin
suspend fun updateDriverInfo(
    driverInfo: DriverInfo,
    newPassword: String? = null
): DriverResult {
    try {
        // Update Firestore
        val updateData = hashMapOf<String, Any>(
            "name" to driverInfo.name,
            "username" to driverInfo.username,  // NEW
            "email" to driverInfo.email,
            // ... rest
        )
        firestore.collection("drivers").document(driverInfo.uid)
            .update(updateData).await()
        
        // Update password if provided
        if (newPassword != null && newPassword.isNotBlank()) {
            val user = auth.currentUser
            if (user != null && user.uid == driverInfo.uid) {
                user.updatePassword(newPassword).await()
            } else {
                // Admin updating another user's password
                // Requires Firebase Admin SDK or Cloud Function
                return DriverResult.Error("Password update requires user reauthentication")
            }
        }
        
        return DriverResult.Success("Driver updated successfully", driverInfo.uid)
    } catch (e: Exception) {
        return DriverResult.Error("Update failed: ${e.message}")
    }
}
```

### 6. Get Current User Info - Include Username

#### Update getCurrentDriverInfo()
```kotlin
suspend fun getCurrentDriverInfo(): DriverInfo? {
    val driverDoc = firestore.collection("drivers").document(uid).get().await()
    
    return DriverInfo(
        uid = uid,
        name = driverDoc.getString("name") ?: "",
        username = driverDoc.getString("username") ?: "",  // NEW
        email = driverDoc.getString("email") ?: "",
        // ... rest
    )
}
```

#### Similar updates for:
- getCurrentStudentInfo()
- getAdminInfo()
- getAllDrivers()
- getAllStudents()

### 7. Navigation Fix (ALREADY DONE ✅)
The navigation from Bus Assignment Login → Bus Operations Hub is correctly implemented with route parameters.

## IMPLEMENTATION ORDER

1. ✅ Update data classes (DONE)
2. Update FirebaseManager authentication functions
3. Update FirebaseManager create/update functions
4. Update all login screens (Admin, Driver, Student)
5. Update Add screens (AddDriver, AddStudent)
6. Update Database screens with edit username/password
7. Test complete flow

## TESTING CHECKLIST

- [ ] Admin can login with username
- [ ] Driver can login with username
- [ ] Student can login with username
- [ ] Bus Assignment Login works (already working)
- [ ] Navigation to Bus Operations Hub works
- [ ] Admin can create users with usernames
- [ ] Admin can edit usernames
- [ ] Admin can change passwords
- [ ] No email addresses visible in UI
- [ ] All existing users migrated (add username field to existing Firestore docs)

## MIGRATION SCRIPT NEEDED

For existing users in Firestore, run a one-time migration to add username field:
```kotlin
// Generate username from email: "john@example.com" → "john"
val username = email.substringBefore("@")
```

## SECURITY NOTES

- Email remains in Firestore for Firebase Auth
- Username must be unique (add Firestore index)
- Password updates require proper authentication
- Consider adding username validation (alphanumeric, no spaces)
