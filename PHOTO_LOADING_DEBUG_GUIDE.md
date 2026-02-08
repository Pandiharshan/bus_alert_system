# Driver Photo Loading - Complete Debug & Fix Guide

## Problem Statement
- ✅ Photo uploads successfully to Firebase Storage
- ✅ photoUrl saved in Firestore
- ❌ Photo does NOT display on Driver Home Page after login

---

## Step-by-Step Debugging Process

### Step 1: Verify Firebase Storage Upload

**Check in Firebase Console:**
1. Go to Firebase Console → Storage
2. Navigate to `driver_photos/` folder
3. Verify file exists: `{driverUID}.jpg`
4. Click on file → Copy download URL
5. Paste URL in browser - should show image

**Expected URL Format:**
```
https://firebasestorage.googleapis.com/v0/b/YOUR_PROJECT.appspot.com/o/driver_photos%2F{UID}.jpg?alt=media&token={TOKEN}
```

**If file doesn't exist:**
- Photo upload failed
- Check `uploadDriverPhoto()` function
- Check Storage permissions

---

### Step 2: Verify Firestore photoUrl Field

**Check in Firebase Console:**
1. Go to Firestore → `drivers` collection
2. Find driver document by UID
3. Check `photoUrl` field value

**Expected:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "photoUrl": "https://firebasestorage.googleapis.com/v0/b/...",
  "assignedBusId": "bus_01",
  "isActive": false
}
```

**Common Issues:**
- ❌ `photoUrl: ""` (empty string) - Upload failed
- ❌ `photoUrl: null` - Field not created
- ❌ `photoUrl: "file:///..."` - Local URI saved instead of download URL
- ✅ `photoUrl: "https://firebasestorage..."` - Correct!

---

### Step 3: Test Photo URL Directly

**Manual Test:**
1. Copy photoUrl from Firestore
2. Paste in browser
3. Should display image immediately

**If image doesn't load in browser:**
- URL is invalid
- Storage permissions issue
- Token expired (shouldn't happen)

**If image loads in browser but not in app:**
- App issue (Coil, permissions, etc.)

---

### Step 4: Check App Logs

**Add Debug Logging:**

Add this to `DriverHomeScreen.kt`:

```kotlin
LaunchedEffect(Unit) {
    scope.launch {
        driverInfo = FirebaseManager.getCurrentDriverInfo()
        
        // DEBUG: Print driver info
        Log.d("DriverHome", "Driver Info: $driverInfo")
        Log.d("DriverHome", "Photo URL: ${driverInfo?.photoUrl}")
        Log.d("DriverHome", "Photo URL isEmpty: ${driverInfo?.photoUrl?.isEmpty()}")
        Log.d("DriverHome", "Photo URL isBlank: ${driverInfo?.photoUrl?.isBlank()}")
        
        if (driverInfo?.assignedBusId?.isNotEmpty() == true) {
            busInfo = FirebaseManager.getBusInfo(driverInfo!!.assignedBusId)
        }
        isLoading = false
    }
}
```

**Check Logcat:**
```bash
adb logcat | grep "DriverHome"
```

**Expected Output:**
```
D/DriverHome: Driver Info: DriverInfo(uid=abc123, name=John Doe, ...)
D/DriverHome: Photo URL: https://firebasestorage.googleapis.com/...
D/DriverHome: Photo URL isEmpty: false
D/DriverHome: Photo URL isBlank: false
```

**If photoUrl is empty/null:**
- Firestore fetch issue
- Check `getCurrentDriverInfo()` function

---

### Step 5: Check Coil Image Loading

**Add Coil Debug Logging:**

In `DriverHomeScreen.kt`, update SubcomposeAsyncImage:

```kotlin
SubcomposeAsyncImage(
    model = ImageRequest.Builder(context)
        .data(photoUrl)
        .crossfade(true)
        .listener(
            onStart = {
                Log.d("CoilImage", "Loading started: $photoUrl")
            },
            onSuccess = { _, _ ->
                Log.d("CoilImage", "Loading SUCCESS")
            },
            onError = { _, result ->
                Log.e("CoilImage", "Loading FAILED: ${result.throwable.message}")
            }
        )
        .build(),
    // ... rest of code
)
```

**Check Logcat:**
```bash
adb logcat | grep "CoilImage"
```

**Possible Errors:**
- `Unable to resolve host` - Network issue
- `403 Forbidden` - Storage permissions issue
- `404 Not Found` - Wrong URL or file deleted
- `SSL handshake failed` - Certificate issue

---

## Common Issues & Fixes

### Issue 1: Empty photoUrl in Firestore

**Cause:** Photo upload failed but driver was still created

**Fix:** Update `createDriverAccount()` to handle upload failure:

```kotlin
suspend fun createDriverAccount(
    driverData: DriverData,
    password: String,
    photoUri: Uri? = null
): DriverResult {
    return try {
        val authResult = auth.createUserWithEmailAndPassword(driverData.email, password).await()
        val driverUid = authResult.user?.uid ?: return DriverResult.Error("Failed to create account")
        
        var photoUrl = ""
        if (photoUri != null) {
            photoUrl = uploadDriverPhoto(driverUid, photoUri) ?: ""
            Log.d("FirebaseManager", "Photo uploaded: $photoUrl")
        }
        
        val driverDocument = hashMapOf(
            "name" to driverData.name,
            "email" to driverData.email,
            "phone" to driverData.phone,
            "photoUrl" to photoUrl,  // Will be empty string if upload failed
            "assignedBusId" to driverData.assignedBusId,
            "isActive" to false,
            "createdAt" to System.currentTimeMillis()
        )
        
        firestore.collection("drivers").document(driverUid).set(driverDocument).await()
        
        DriverResult.Success("Driver account created successfully", driverUid)
    } catch (e: Exception) {
        Log.e("FirebaseManager", "Create driver failed", e)
        DriverResult.Error(getErrorMessage(e))
    }
}
```

---

### Issue 2: Storage Permissions

**Check Firebase Storage Rules:**

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /driver_photos/{driverUid}.jpg {
      // Allow anyone authenticated to read
      allow read: if request.auth != null;
      
      // Allow admins to write
      allow write: if request.auth != null;
    }
  }
}
```

**If rules are too restrictive:**
- Photos won't load
- Update rules to allow authenticated read

---

### Issue 3: Coil Not Loading HTTPS URLs

**Add Coil Configuration:**

Create `CampusBusBuddyApplication.kt`:

```kotlin
package com.campusbussbuddy.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

class CampusBusBuddyApplication : Application(), ImageLoaderFactory {
    
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50 MB
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
```

**Verify in AndroidManifest.xml:**
```xml
<application
    android:name=".app.CampusBusBuddyApplication"
    ...>
```

---

### Issue 4: Network Security Configuration

**For Android 9+ (API 28+):**

Create `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    
    <!-- Trust Firebase Storage -->
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">firebasestorage.googleapis.com</domain>
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </domain-config>
</network-security-config>
```

**Add to AndroidManifest.xml:**
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

---

## Complete Fixed Implementation

### 1. Updated FirebaseManager.kt

```kotlin
/**
 * Upload driver photo to Firebase Storage
 */
private suspend fun uploadDriverPhoto(driverUid: String, photoUri: Uri): String? {
    return try {
        Log.d("FirebaseManager", "Starting photo upload for UID: $driverUid")
        Log.d("FirebaseManager", "Photo URI: $photoUri")
        
        val storageRef = storage.reference.child("driver_photos/$driverUid.jpg")
        
        // Upload file
        val uploadTask = storageRef.putFile(photoUri).await()
        Log.d("FirebaseManager", "Upload task completed: ${uploadTask.metadata?.path}")
        
        // Get download URL
        val downloadUrl = storageRef.downloadUrl.await().toString()
        Log.d("FirebaseManager", "Download URL: $downloadUrl")
        
        return downloadUrl
    } catch (e: Exception) {
        Log.e("FirebaseManager", "Photo upload failed", e)
        null
    }
}

/**
 * Get current driver information
 */
suspend fun getCurrentDriverInfo(): DriverInfo? {
    return try {
        val currentUser = auth.currentUser ?: return null
        val driverDoc = firestore.collection("drivers").document(currentUser.uid).get().await()
        
        if (driverDoc.exists()) {
            val photoUrl = driverDoc.getString("photoUrl") ?: ""
            Log.d("FirebaseManager", "Fetched driver photoUrl: $photoUrl")
            
            DriverInfo(
                uid = currentUser.uid,
                name = driverDoc.getString("name") ?: "Driver",
                email = driverDoc.getString("email") ?: "",
                phone = driverDoc.getString("phone") ?: "",
                photoUrl = photoUrl,
                assignedBusId = driverDoc.getString("assignedBusId") ?: "",
                isActive = driverDoc.getBoolean("isActive") ?: false
            )
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("FirebaseManager", "Get driver info failed", e)
        null
    }
}
```

---

### 2. Updated DriverHomeScreen.kt

```kotlin
@Composable
private fun DriverProfileSection(
    driverInfo: DriverInfo?,
    busInfo: BusInfo?
) {
    val context = LocalContext.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E4A5A))
                .border(3.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val photoUrl = driverInfo?.photoUrl?.trim() ?: ""
            
            // Debug log
            Log.d("DriverProfileSection", "Photo URL: '$photoUrl'")
            Log.d("DriverProfileSection", "Is empty: ${photoUrl.isEmpty()}")
            Log.d("DriverProfileSection", "Is blank: ${photoUrl.isBlank()}")
            
            if (photoUrl.isNotEmpty() && photoUrl.isNotBlank()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photoUrl)
                        .crossfade(true)
                        .memoryCacheKey(photoUrl)
                        .diskCacheKey(photoUrl)
                        .listener(
                            onStart = {
                                Log.d("CoilImage", "Loading started")
                            },
                            onSuccess = { _, _ ->
                                Log.d("CoilImage", "Loading SUCCESS")
                            },
                            onError = { _, result ->
                                Log.e("CoilImage", "Loading FAILED: ${result.throwable.message}")
                                result.throwable.printStackTrace()
                            }
                        )
                        .build(),
                    contentDescription = "Driver Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Default Profile",
                                modifier = Modifier.size(60.dp),
                                tint = Color.White
                            )
                        }
                    }
                )
            } else {
                Log.d("DriverProfileSection", "Showing default icon - no photo URL")
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }
        }
        
        // ... rest of code
    }
}
```

---

## Testing Checklist

### Test 1: Create New Driver with Photo
- [ ] Admin adds driver with photo
- [ ] Check Firebase Storage - file exists
- [ ] Check Firestore - photoUrl field has HTTPS URL
- [ ] Copy URL, paste in browser - image loads
- [ ] Login as driver
- [ ] Check Logcat for debug messages
- [ ] Photo displays on Driver Home

### Test 2: Create Driver Without Photo
- [ ] Admin adds driver without photo
- [ ] Check Firestore - photoUrl is empty string
- [ ] Login as driver
- [ ] Default icon displays (not broken image)

### Test 3: Network Issues
- [ ] Turn off WiFi
- [ ] Login as driver
- [ ] Should show loading spinner, then default icon
- [ ] Turn on WiFi
- [ ] Pull to refresh (if implemented)
- [ ] Photo should load

### Test 4: Invalid URL
- [ ] Manually edit Firestore photoUrl to invalid URL
- [ ] Login as driver
- [ ] Should show default icon (error state)
- [ ] Check Logcat for error message

---

## Quick Fix Checklist

If photo still doesn't load, check these in order:

1. **Firebase Storage:**
   - [ ] File exists in `driver_photos/{UID}.jpg`
   - [ ] Storage rules allow authenticated read
   - [ ] URL works in browser

2. **Firestore:**
   - [ ] photoUrl field exists
   - [ ] photoUrl is HTTPS URL (not empty/null)
   - [ ] photoUrl starts with `https://firebasestorage.googleapis.com`

3. **App Permissions:**
   - [ ] `INTERNET` permission in AndroidManifest
   - [ ] `ACCESS_NETWORK_STATE` permission in AndroidManifest

4. **Coil Configuration:**
   - [ ] Coil dependency in build.gradle
   - [ ] ImageRequest properly configured
   - [ ] Error handling in place

5. **Code Logic:**
   - [ ] `getCurrentDriverInfo()` returns photoUrl
   - [ ] photoUrl is not empty/blank before loading
   - [ ] SubcomposeAsyncImage receives correct URL

6. **Logs:**
   - [ ] Check Logcat for errors
   - [ ] Verify photo URL is correct
   - [ ] Check Coil error messages

---

## Emergency Workaround

If nothing works, use this temporary solution to verify the issue:

```kotlin
// In DriverHomeScreen.kt
val testUrl = "https://firebasestorage.googleapis.com/v0/b/YOUR_PROJECT.appspot.com/o/driver_photos%2FTEST_UID.jpg?alt=media&token=TEST_TOKEN"

SubcomposeAsyncImage(
    model = testUrl,  // Use hardcoded URL
    // ... rest of code
)
```

**If hardcoded URL works:**
- Issue is with fetching photoUrl from Firestore
- Check `getCurrentDriverInfo()` function

**If hardcoded URL doesn't work:**
- Issue is with Coil or app configuration
- Check permissions, Coil setup, network config

---

## Final Solution

After debugging, the most common fix is ensuring:

1. **Trim whitespace from photoUrl**
2. **Check for empty string vs null**
3. **Add proper error logging**
4. **Configure Coil properly**
5. **Set correct Storage permissions**

The updated code I provided above includes all these fixes!
