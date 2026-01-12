# Create base package structure
$basePath = "app\src\main\java\com\campusbussbuddy"

# Create all directories first
@(
    # Main directories
    "app",
    "di",
    
    # UI
    "ui/screens/auth",
    "ui/screens/student",
    "ui/screens/driver",
    "ui/screens/common",
    "ui/components",
    "ui/navigation",
    
    # ViewModels
    "viewmodel/auth",
    "viewmodel/student",
    "viewmodel/driver",
    
    # Domain
    "domain/model",
    "domain/usecase",
    
    # Data
    "data/repository",
    "data/local/Dao",
    "data/remote",
    
    # Other packages
    "location",
    "notification",
    "utils",
    "theme"
) | ForEach-Object {
    $directory = Join-Path $basePath $_
    if (-not (Test-Path -Path $directory)) {
        New-Item -ItemType Directory -Path $directory -Force | Out-Null
    }
}

# Create empty .kt files
@(
    # Data
    "data/local/AppDatabase.kt",
    "data/remote/FirebaseService.kt",
    
    # Location
    "location/MapsManager.kt",
    "location/LocationService.kt",
    "location/LocationManager.kt",
    
    # Notification
    "notification/NotificationService.kt",
    "notification/AlertManager.kt",
    
    # Utils
    "utils/QrGenerator.kt",
    "utils/QrScanner.kt"
) | ForEach-Object {
    $filePath = Join-Path $basePath $_
    $directory = [System.IO.Path]::GetDirectoryName($filePath)
    
    # Create directory if it doesn't exist
    if (-not (Test-Path -Path $directory)) {
        New-Item -ItemType Directory -Path $directory -Force | Out-Null
    }
    
    # Create empty file
    New-Item -ItemType File -Path $filePath -Force | Out-Null
}

Write-Host "Project structure created successfully!"
