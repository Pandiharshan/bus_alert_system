# Firebase Admin Authentication Setup

## Required Firebase Configuration

### 1. Firebase Console Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `Campus Bus Buddy`
3. Enable Authentication and Firestore Database

### 2. Create Admin Account
**In Firebase Console > Authentication > Users:**
1. Click "Add User"
2. Email: `admin@campusbuddy.com` (or your preferred admin email)
3. Password: Create a secure password
4. Click "Add User"

### 3. Create Admin Collection (Optional - for enhanced security)
**In Firebase Console > Firestore Database:**
1. Create collection: `admins`
2. Add document with the admin user's UID as document ID
3. Add fields:
   ```
   email: "admin@campusbuddy.com"
   role: "admin"
   createdAt: [current timestamp]
   ```

### 4. Security Rules (Recommended)
**Firestore Rules:**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Only authenticated admins can read admin collection
    match /admins/{adminId} {
      allow read: if request.auth != null && request.auth.uid == adminId;
    }
    
    // Other collections rules...
  }
}
```

## How the Authentication Works

### 1. User Login Flow
1. User enters email and password on Admin Login screen
2. Firebase Auth attempts to sign in with credentials
3. If successful, app checks if user exists in `admins` collection
4. If verified, user is granted access to Admin Home
5. If not verified, user is logged out and access denied

### 2. Security Features
- ✅ No hardcoded credentials in app
- ✅ Firebase Authentication handles password security
- ✅ Additional verification via Firestore collection
- ✅ Automatic logout on access denial
- ✅ Clean error messages for users
- ✅ Loading states during authentication

### 3. Fallback Authentication
If `admins` collection doesn't exist, the app falls back to checking:
- Email matches: `admin@campusbuddy.com`

## Testing the Implementation

### 1. Valid Admin Login
- Email: `admin@campusbuddy.com`
- Password: [Your Firebase password]
- Expected: Navigate to Admin Home

### 2. Invalid Credentials
- Wrong email/password
- Expected: Show error message

### 3. Unauthorized User
- Valid Firebase user but not in admins collection
- Expected: "Access denied" message and logout

## Security Best Practices Implemented

1. **No Hardcoded Credentials**: All credentials managed in Firebase Console
2. **Dual Verification**: Firebase Auth + Firestore collection check
3. **Automatic Logout**: Unauthorized users are immediately logged out
4. **Clean Error Handling**: User-friendly error messages
5. **Loading States**: Professional UI feedback during authentication
6. **Single Admin Account**: Only one admin account supported as requested

## Admin Account Management

To change admin credentials:
1. Go to Firebase Console > Authentication
2. Find the admin user
3. Update email/password as needed
4. Update Firestore `admins` collection if used

To add additional admins (if needed in future):
1. Create new user in Firebase Auth
2. Add their UID to `admins` collection in Firestore