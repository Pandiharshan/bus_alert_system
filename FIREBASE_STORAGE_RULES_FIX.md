# Firebase Storage Rules - Quick Fix Guide

## Problem: Photos Upload but Don't Display

If photos upload successfully to Firebase Storage but don't display in the app, the most common cause is **incorrect Storage Rules**.

---

## How to Check Current Rules

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **Campus Bus Buddy**
3. Click **Storage** in left sidebar
4. Click **Rules** tab at the top
5. Check what rules are currently set

---

## Common Problematic Rules

### ❌ BAD: Deny All Access
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if false;  // Nothing works!
    }
  }
}
```

### ❌ BAD: Only Allow During Testing
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.time < timestamp.date(2024, 1, 1);  // Expired!
    }
  }
}
```

---

## ✅ CORRECT Rules for Campus Bus Buddy

Copy and paste these rules into Firebase Console:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    // Driver Photos - Allow authenticated users to read and write
    match /driver_photos/{driverUid}.jpg {
      // Any authenticated user can read driver photos
      allow read: if request.auth != null;
      
      // Any authenticated user can upload/update driver photos
      // In production, you may want to restrict write to admins only
      allow write: if request.auth != null;
    }
    
    // Optional: Add rules for other folders as needed
    // match /bus_photos/{busId}.jpg {
    //   allow read: if request.auth != null;
    //   allow write: if request.auth != null;
    // }
    
    // Deny access to everything else
    match /{allPaths=**} {
      allow read, write: if false;
    }
  }
}
```

---

## More Secure Rules (Admin-Only Upload)

If you want only admins to upload/edit driver photos:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    // Helper function to check if user is admin
    function isAdmin() {
      return request.auth != null && 
             exists(/databases/(default)/documents/admins/$(request.auth.token.email));
    }
    
    // Driver Photos
    match /driver_photos/{driverUid}.jpg {
      // Any authenticated user can read
      allow read: if request.auth != null;
      
      // Only admins can write
      allow write: if isAdmin();
    }
    
    // Deny everything else
    match /{allPaths=**} {
      allow read, write: if false;
    }
  }
}
```

---

## How to Update Rules

1. Go to Firebase Console → Storage → Rules
2. **Delete all existing rules**
3. **Copy and paste** the correct rules above
4. Click **Publish** button
5. Wait 10-30 seconds for rules to propagate
6. **Test the app again**

---

## How to Test if Rules Work

### Test 1: Direct URL in Browser

1. Get a photo URL from Firestore (drivers collection → photoUrl field)
2. Copy the full URL (should start with `https://firebasestorage.googleapis.com/`)
3. Paste in browser
4. **Expected:** Image displays immediately
5. **If 403 Forbidden:** Rules are blocking access

### Test 2: In the App

1. Login as driver
2. Check Logcat for Coil errors
3. **Expected:** `D/CoilImage: Loading SUCCESS`
4. **If 403 error:** Rules are blocking access

---

## Common Rule Mistakes

### Mistake 1: Wrong Path Pattern

❌ **Wrong:**
```javascript
match /driver_photos/{file} {  // Matches any filename
```

✅ **Correct:**
```javascript
match /driver_photos/{driverUid}.jpg {  // Matches only .jpg files
```

### Mistake 2: Forgetting Authentication Check

❌ **Wrong:**
```javascript
allow read: if true;  // Anyone can read, even unauthenticated!
```

✅ **Correct:**
```javascript
allow read: if request.auth != null;  // Only authenticated users
```

### Mistake 3: Too Restrictive

❌ **Wrong:**
```javascript
allow read: if request.auth.uid == driverUid;  // Only owner can read
```

✅ **Correct:**
```javascript
allow read: if request.auth != null;  // Any authenticated user can read
```

**Why?** Admins need to see all driver photos in the database screen!

---

## Quick Checklist

Before testing the app, verify:

- [ ] Storage Rules allow authenticated read
- [ ] Storage Rules allow authenticated write (or admin-only)
- [ ] Rules are published (not just saved)
- [ ] Waited 30 seconds after publishing
- [ ] Photo file exists in Storage Console
- [ ] photoUrl in Firestore is HTTPS URL
- [ ] App has INTERNET permission (already present)
- [ ] User is authenticated when viewing photos

---

## Still Not Working?

If photos still don't load after fixing rules:

1. **Check Logcat** for specific error messages
2. **Verify photo URL format** in Firestore
3. **Test URL in browser** to isolate the issue
4. **Check network connectivity** on device
5. **Review the debug logs** added in the recent fix

See `PHOTO_LOADING_FIX_APPLIED.md` for detailed debugging steps.

---

## Production Best Practices

### Security Recommendations:

1. **Restrict write access to admins only** (use the secure rules above)
2. **Validate file size** to prevent abuse:
   ```javascript
   allow write: if request.resource.size < 5 * 1024 * 1024;  // Max 5MB
   ```
3. **Validate file type**:
   ```javascript
   allow write: if request.resource.contentType.matches('image/.*');
   ```
4. **Add rate limiting** using Firebase App Check

### Complete Production Rules:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    function isAdmin() {
      return request.auth != null && 
             exists(/databases/(default)/documents/admins/$(request.auth.token.email));
    }
    
    match /driver_photos/{driverUid}.jpg {
      // Any authenticated user can read
      allow read: if request.auth != null;
      
      // Only admins can write, with size and type validation
      allow write: if isAdmin() && 
                      request.resource.size < 5 * 1024 * 1024 &&
                      request.resource.contentType.matches('image/.*');
    }
    
    match /{allPaths=**} {
      allow read, write: if false;
    }
  }
}
```

---

## Summary

**Most Common Fix:** Update Storage Rules to allow authenticated read access.

**How to Apply:**
1. Go to Firebase Console → Storage → Rules
2. Paste the correct rules
3. Click Publish
4. Wait 30 seconds
5. Test the app

This fixes 90% of photo loading issues!
