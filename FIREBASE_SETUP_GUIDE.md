# Firebase Setup Guide for Campus Bus Buddy

## Step 1: Create Firebase Project

### 1.1 Go to Firebase Console
1. Visit [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Project name: `Campus Bus Buddy`
4. Enable Google Analytics (optional)
5. Click "Create project"

### 1.2 Add Android App
1. In Firebase Console, click "Add app" → Android icon
2. **Android package name:** `com.campusbussbuddy`
3. **App nickname:** `Campus Bus Buddy`
4. **Debug signing certificate SHA-1:** (Optional for now)
5. Click "Register app"

### 1.3 Download google-services.json
1. Download the `google-services.json` file
2. Place it in your app module: `app/google-services.json`
3. **IMPORTANT:** This file is already in your project, but replace it with the new one

## Step 2: Enable Firebase Services

### 2.1 Enable Authentication
1. In Firebase Console → Authentication
2. Click "Get started"
3. Go to "Sign-in method" tab
4. Enable "Email/Password" provider
5. Click "Save"

### 2.2 Enable Firestore Database
1. In Firebase Console → Firestore Database
2. Click "Create database"
3. Choose "Start in test mode" (we'll secure it later)
4. Select a location (choose closest to your users)
5. Click "Done"

## Step 3: Create Admin Account

### 3.1 Create Admin User in Authentication
1. Go to Firebase Console → Authentication → Users
2. Click "Add user"
3. **Email:** `admin@campusbuddy.com`
4. **Password:** Create a strong password (save it securely)
5. Click "Add user"
6. **Copy the User UID** - you'll need this

### 3.2 Create Admin Document in Firestore
1. Go to Firebase Console → Firestore Database
2. Click "Start collection"
3. **Collection ID:** `admins`
4. **Document ID:** Use the User UID from step 3.1
5. Add fields:
   ```
   email: admin@campusbuddy.com
   role: admin
   name: System Administrator
   createdAt: [current timestamp]
   isActive: true
   ```
6. Click "Save"

## Step 4: Configure Security Rules

### 4.1 Firestore Security Rules
1. Go to Firebase Console → Firestore Database → Rules
2. Replace the rules with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Admin collection - only authenticated admins can read their own document
    match /admins/{adminId} {
      allow read: if request.auth != null && request.auth.uid == adminId;
      allow write: if false; // No writes allowed from client
    }
    
    // Buses collection - only admins can read/write
    match /buses/{busId} {
      allow read, write: if request.auth != null && 
        exists(/databases/$(database)/documents/admins/$(request.auth.uid));
    }
    
    // Drivers collection - only admins can read/write
    match /drivers/{driverId} {
      allow read, write: if request.auth != null && 
        exists(/databases/$(database)/documents/admins/$(request.auth.uid));
    }
    
    // Students collection - only admins can read/write
    match /students/{studentId} {
      allow read, write: if request.auth != null && 
        exists(/databases/$(database)/documents/admins/$(request.auth.uid));
    }
    
    // Attendance collection - only admins can read/write
    match /attendance/{attendanceId} {
      allow read, write: if request.auth != null && 
        exists(/databases/$(database)/documents/admins/$(request.auth.uid));
    }
    
    // Trips collection - only admins can read/write
    match /trips/{tripId} {
      allow read, write: if request.auth != null && 
        exists(/databases/$(database)/documents/admins/$(request.auth.uid));
    }
  }
}
```

3. Click "Publish"

## Step 5: Test Firebase Connection

### 5.1 Verify google-services.json
Ensure your `app/google-services.json` contains:
```json
{
  "project_info": {
    "project_id": "your-project-id",
    "project_number": "your-project-number"
  },
  "client": [
    {
      "client_info": {
        "android_client_info": {
          "package_name": "com.campusbussbuddy"
        }
      }
    }
  ]
}
```

### 5.2 Build and Test
1. Clean and rebuild your project
2. Run the app
3. Navigate to Admin Login
4. Try logging in with:
   - **Email:** `admin@campusbuddy.com`
   - **Password:** [Your created password]

## Step 6: Database Structure (Optional - for future use)

### 6.1 Create Collections Structure
You can pre-create these collections in Firestore:

```
📁 admins/
  📄 [admin-uid]
    - email: string
    - role: string
    - name: string
    - createdAt: timestamp
    - isActive: boolean

📁 buses/
  📄 [bus-id]
    - busNumber: string
    - capacity: number
    - driverId: string
    - route: string
    - isActive: boolean
    - createdAt: timestamp

📁 drivers/
  📄 [driver-id]
    - name: string
    - email: string
    - phone: string
    - licenseNumber: string
    - assignedBusId: string
    - isActive: boolean
    - createdAt: timestamp

📁 students/
  📄 [student-id]
    - name: string
    - studentId: string
    - email: string
    - phone: string
    - assignedBusId: string
    - isActive: boolean
    - createdAt: timestamp

📁 trips/
  📄 [trip-id]
    - busId: string
    - driverId: string
    - startTime: timestamp
    - endTime: timestamp
    - route: string
    - status: string (active, completed, cancelled)
    - studentsOnboard: array

📁 attendance/
  📄 [attendance-id]
    - studentId: string
    - tripId: string
    - busId: string
    - checkInTime: timestamp
    - checkOutTime: timestamp
    - date: string
    - status: string (present, absent, late)
```

## Step 7: Troubleshooting

### 7.1 Common Issues
1. **Build errors:** Ensure `google-services.json` is in correct location
2. **Authentication fails:** Check if Email/Password is enabled in Firebase Console
3. **Access denied:** Verify admin document exists in Firestore with correct UID
4. **Network errors:** Check internet connection and Firebase project status

### 7.2 Debug Steps
1. Check Firebase Console → Authentication → Users (admin should be listed)
2. Check Firebase Console → Firestore → admins collection (admin document should exist)
3. Check Android Studio logs for Firebase connection errors
4. Verify package name matches in `google-services.json` and `build.gradle.kts`

## Step 8: Security Best Practices

### 8.1 Implemented Security
✅ No hardcoded credentials
✅ Firebase Authentication for secure login
✅ Firestore rules prevent unauthorized access
✅ Admin verification via Firestore document
✅ Automatic logout for unauthorized users
✅ Single admin account restriction

### 8.2 Additional Security (Optional)
- Enable App Check for additional security
- Set up Firebase Security Rules testing
- Monitor authentication events in Firebase Console
- Regular security rule audits

## Admin Credentials
**Email:** `admin@campusbuddy.com`
**Password:** [Set in Firebase Console - keep secure]

## Next Steps
1. Complete Firebase setup following this guide
2. Test admin login functionality
3. Verify access to Admin Home Page
4. Begin implementing admin management features

---

**Important:** Keep your Firebase project credentials secure and never commit sensitive information to version control.