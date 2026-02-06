# Driver System Test Guide

## System Overview

The driver system has been fully implemented with Firebase Authentication and Firestore integration. This guide will help you test the complete driver flow.

## Prerequisites

1. Firebase project is configured
2. `google-services.json` is in place
3. Firebase Authentication is enabled
4. Firestore database is created with collections: `drivers/` and `buses/`

## Test Flow

### Step 1: Prepare Test Bus Data

Create a test bus in Firestore Console:

**Collection:** `buses`  
**Document ID:** `bus_01`  
**Fields:**
```json
{
  "busNumber": 15,
  "capacity": 50,
  "activeDriverId": "",
  "activeDriverName": "",
  "activeDriverPhone": ""
}
```

### Step 2: Admin Creates Driver Account

**Method 1: Using Firebase Console (Manual Testing)**

1. Go to Firebase Console → Authentication
2. Click "Add User"
3. Enter:
   - Email: `testdriver@campusbuddy.com`
   - Password: `TestDriver123`
4. Copy the generated UID (e.g., `abc123xyz456`)

5. Go to Firestore → Create document in `drivers` collection
6. Document ID: Use the UID from step 4
7. Add fields:
```json
{
  "name": "John Driver",
  "email": "testdriver@campusbuddy.com",
  "phone": "+1234567890",
  "photoUrl": "",
  "assignedBusId": "bus_01",
  "isActive": false,
  "createdAt": 1234567890000
}
```

**Method 2: Using FirebaseManager (Programmatic - Future Admin UI)**

```kotlin
// This will be called from Admin UI when "Add Driver" button is clicked
val driverData = DriverData(
    name = "John Driver",
    email = "testdriver@campusbuddy.com",
    phone = "+1234567890",
    assignedBusId = "bus_01"
)

when (val result = FirebaseManager.createDriverAccount(
    driverData = driverData,
    password = "TestDriver123",
    photoUri = null // or provide Uri for photo
)) {
    is DriverResult.Success -> {
        println("Driver created with UID: ${result.driverUid}")
    }
    is DriverResult.Error -> {
        println("Error: ${result.message}")
    }
}
```

### Step 3: Test Driver Login

1. Launch the app
2. On Login Selection Screen, click "Driver Access"
3. On Driver Authentication Screen, enter:
   - **Email:** `testdriver@campusbuddy.com`
   - **Password:** `TestDriver123`
4. Click "Start Shift"

**Expected Behavior:**
- Loading indicator appears
- Firebase authenticates the driver
- System fetches driver data from Firestore
- Navigation to Driver Home Page
- Driver name "John Driver" is displayed
- Bus number "15" is shown
- Employee ID (first 8 chars of UID) is displayed

### Step 4: Verify Driver Home Page Data

**Check the following on Driver Home Page:**

✅ Driver name displays correctly: "John Driver"  
✅ Employee ID shows (first 8 characters of UID)  
✅ Bus number displays: "Bus 15"  
✅ Profile icon shows (or photo if uploaded)

### Step 5: Test Error Scenarios

**Test 1: Wrong Password**
- Email: `testdriver@campusbuddy.com`
- Password: `WrongPassword`
- Expected: Error message "Incorrect password. Please try again."

**Test 2: Non-existent Email**
- Email: `nonexistent@campusbuddy.com`
- Password: `TestDriver123`
- Expected: Error message "No admin account found with this email."

**Test 3: Empty Fields**
- Leave email or password empty
- Expected: Button remains disabled

**Test 4: Driver Profile Not Found**
- Create Auth user but don't create Firestore document
- Expected: Error message "Driver profile not found"

## Firebase Data Verification

### Check Authentication
1. Go to Firebase Console → Authentication
2. Verify user exists with email `testdriver@campusbuddy.com`
3. Note the UID

### Check Firestore
1. Go to Firebase Console → Firestore
2. Navigate to `drivers/{UID}`
3. Verify all fields are present:
   - name
   - email
   - phone
   - photoUrl
   - assignedBusId
   - isActive
   - createdAt

### Check Bus Assignment
1. Navigate to `buses/bus_01`
2. Verify fields:
   - busNumber: 15
   - capacity: 50
   - activeDriverId: "" (empty until driver activates)
   - activeDriverName: ""
   - activeDriverPhone: ""

## Testing Bus Lock System (Future Feature)

When bus activation is implemented:

1. Driver 1 logs in and activates bus
2. Check `buses/bus_01`:
   - activeDriverId: {driver1_uid}
   - activeDriverName: "John Driver"
   - activeDriverPhone: "+1234567890"

3. Driver 2 tries to activate same bus
4. Expected: Error "Bus is currently active with John Driver"

## Common Issues and Solutions

### Issue 1: "Driver profile not found"
**Cause:** Firestore document doesn't exist or UID mismatch  
**Solution:** Ensure Firestore document ID matches Auth UID exactly

### Issue 2: "Authentication failed"
**Cause:** Wrong credentials or Auth user doesn't exist  
**Solution:** Verify email/password in Firebase Console

### Issue 3: Driver name not showing
**Cause:** Firestore document missing or name field empty  
**Solution:** Check Firestore document has "name" field

### Issue 4: Bus number shows "Not Assigned"
**Cause:** assignedBusId is empty or bus document doesn't exist  
**Solution:** Verify assignedBusId matches bus document ID

### Issue 5: Photo not loading
**Cause:** photoUrl is empty or invalid  
**Solution:** Upload photo to Firebase Storage and update photoUrl

## Test Checklist

- [ ] Firebase project configured
- [ ] Test bus created in Firestore
- [ ] Driver Auth account created
- [ ] Driver Firestore document created with correct UID
- [ ] Driver can log in with email/password
- [ ] Driver Home Page shows correct name
- [ ] Bus number displays correctly
- [ ] Employee ID shows
- [ ] Error handling works for wrong credentials
- [ ] Loading states work properly

## Next Steps

After driver system is verified:

1. **Admin UI for Adding Drivers**
   - Create "Add Driver" screen in Admin Home
   - Form with name, email, password, phone, bus assignment
   - Photo upload functionality
   - Call `FirebaseManager.createDriverAccount()`

2. **Bus Activation**
   - Implement "Bus Login" button functionality
   - Call `FirebaseManager.activateDriverAndLockBus()`
   - Show active trip status

3. **Driver Dashboard**
   - Add real-time trip tracking
   - Student attendance features
   - Route management

## Security Notes

- Passwords are NEVER stored in Firestore
- Only Firebase Auth handles passwords
- Auth UID is used as Firestore document ID
- Firestore Security Rules should restrict driver access to their own document

## Support

If you encounter issues:
1. Check Firebase Console logs
2. Verify Firestore document structure
3. Ensure Auth UID matches Firestore document ID
4. Check network connectivity
5. Review error messages in app logs
