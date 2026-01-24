# Project Structure Verification Script
Write-Host "=== CAMPUS BUS BUDDY - PROJECT VERIFICATION ===" -ForegroundColor Green

# Check essential files
$essentialFiles = @(
    "app/build.gradle.kts",
    "app/src/main/AndroidManifest.xml",
    "app/src/main/java/com/campusbussbuddy/MainActivity.kt",
    "app/src/main/java/com/campusbussbuddy/app/CampusBusBuddyApplication.kt",
    "app/src/main/java/com/campusbussbuddy/viewmodel/auth/AuthViewModel.kt",
    "app/src/main/java/com/campusbussbuddy/ui/screens/auth/LoginScreen.kt",
    "app/src/main/java/com/campusbussbuddy/ui/screens/auth/RegisterScreen.kt",
    "app/src/main/java/com/campusbussbuddy/ui/navigation/RootNavHost.kt"
)

Write-Host "`n1. CHECKING ESSENTIAL FILES:" -ForegroundColor Yellow
foreach ($file in $essentialFiles) {
    if (Test-Path $file) {
        Write-Host "✓ $file" -ForegroundColor Green
    } else {
        Write-Host "✗ $file" -ForegroundColor Red
    }
}

Write-Host "`n=== PROJECT VERIFICATION COMPLETE ===" -ForegroundColor Green