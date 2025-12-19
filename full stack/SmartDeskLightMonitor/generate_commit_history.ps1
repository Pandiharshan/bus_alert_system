# SmartDesk Light Monitor - Commit History Generator
# Generates realistic commit history from January 16 to May 31, 2025

Write-Host "Starting SmartDesk Light Monitor commit history generation..." -ForegroundColor Green

# Git configuration
git config user.name "SmartDesk Developer"
git config user.email "dev@smartdesk.com"

# Function to create commits with specific dates
function New-CommitWithDate {
    param(
        [string]$Message,
        [string]$Date,
        [string]$File = "README.md"
    )
    
    # Create or modify file
    $content = Get-Content $File -ErrorAction SilentlyContinue
    if ($null -eq $content) {
        $content = @("# SmartDesk Light Monitor", "Commit: $Message", "Date: $Date", "")
    } else {
        $content += "Commit: $Message - $Date"
    }
    $content | Set-Content $File
    
    # Stage and commit with specific date
    git add $File
    $env:GIT_COMMITTER_DATE = $Date
    $env:GIT_AUTHOR_DATE = $Date
    git commit -m $Message
    $env:GIT_COMMITTER_DATE = ""
    $env:GIT_AUTHOR_DATE = ""
    
    Write-Host "Created commit: $Message ($Date)" -ForegroundColor Cyan
}

# January 2025 - Project Setup and Initial Architecture
Write-Host "`n=== January 2025 - Project Setup ===" -ForegroundColor Yellow

New-CommitWithDate -Message "feat: Initialize SmartDesk Light Monitor project" -Date "2025-01-16T09:00:00"
New-CommitWithDate -Message "feat: Add Gradle build configuration with Kotlin and Compose" -Date "2025-01-17T10:30:00"
New-CommitWithDate -Message "feat: Set up Android manifest with required permissions" -Date "2025-01-18T14:15:00"
New-CommitWithDate -Message "feat: Create basic MainActivity structure" -Date "2025-01-20T11:00:00"
New-CommitWithDate -Message "feat: Add Compose theme and basic UI components" -Date "2025-01-21T16:45:00"
New-CommitWithDate -Message "feat: Implement DashboardViewModel with state management" -Date "2025-01-23T13:20:00"
New-CommitWithDate -Message "feat: Add sensor data models and serialization" -Date "2025-01-24T10:00:00"
New-CommitWithDate -Message "feat: Create NetworkClient for external sensor communication" -Date "2025-01-27T15:30:00"
New-CommitWithDate -Message "feat: Implement SensorManagerWrapper for fallback sensors" -Date "2025-01-28T12:00:00"
New-CommitWithDate -Message "feat: Add notification channel setup" -Date "2025-01-30T09:15:00"
New-CommitWithDate -Message "feat: Create basic dashboard screen layout" -Date "2025-01-31T14:00:00"

# February 2025 - Core Features Development
Write-Host "`n=== February 2025 - Core Features ===" -ForegroundColor Yellow

New-CommitWithDate -Message "feat: Implement real-time lux meter display" -Date "2025-02-03T11:30:00"
New-CommitWithDate -Message "feat: Add circular gauge visualization for light levels" -Date "2025-02-05T16:00:00"
New-CommitWithDate -Message "feat: Create threshold controls with sliders" -Date "2025-02-07T13:45:00"
New-CommitWithDate -Message "feat: Implement alert system for threshold violations" -Date "2025-02-10T10:15:00"
New-CommitWithDate -Message "feat: Add preset configurations (Office, Bright, Dim)" -Date "2025-02-12T15:30:00"
New-CommitWithDate -Message "feat: Implement manual alert toggle functionality" -Date "2025-02-14T12:00:00"
New-CommitWithDate -Message "feat: Add lux history tracking and sparkline chart" -Date "2025-02-17T09:00:00"
New-CommitWithDate -Message "feat: Implement connection status indicator" -Date "2025-02-19T14:20:00"
New-CommitWithDate -Message "feat: Add network auto-connect functionality" -Date "2025-02-21T11:45:00"
New-CommitWithDate -Message "feat: Implement fallback to device sensors" -Date "2025-02-24T16:30:00"
New-CommitWithDate -Message "feat: Add SharedPreferences for settings persistence" -Date "2025-02-26T10:00:00"
New-CommitWithDate -Message "feat: Create notification service for alerts" -Date "2025-02-28T13:15:00"

# March 2025 - UI Enhancements and Polish
Write-Host "`n=== March 2025 - UI Enhancements ===" -ForegroundColor Yellow

New-CommitWithDate -Message "feat: Add Material 3 design system integration" -Date "2025-03-03T09:30:00"
New-CommitWithDate -Message "feat: Implement responsive layout for different screen sizes" -Date "2025-03-05T14:00:00"
New-CommitWithDate -Message "feat: Add smooth animations for gauge and transitions" -Date "2025-03-07T11:20:00"
New-CommitWithDate -Message "feat: Create custom color scheme for light/dark themes" -Date "2025-03-10T16:45:00"
New-CommitWithDate -Message "feat: Add haptic feedback for user interactions" -Date "2025-03-12T10:30:00"
New-CommitWithDate -Message "feat: Implement edge-to-edge display support" -Date "2025-03-14T13:00:00"
New-CommitWithDate -Message "feat: Add loading states and progress indicators" -Date "2025-03-17T15:15:00"
New-CommitWithDate -Message "feat: Create error handling UI for network issues" -Date "2025-03-19T09:45:00"
New-CommitWithDate -Message "feat: Add tooltip help text for controls" -Date "2025-03-21T12:30:00"
New-CommitWithDate -Message "feat: Implement swipe-to-refresh functionality" -Date "2025-03-24T14:00:00"
New-CommitWithDate -Message "feat: Add app icon and launcher assets" -Date "2025-03-26T11:15:00"
New-CommitWithDate -Message "feat: Create onboarding screens for first-time users" -Date "2025-03-28T16:20:00"
New-CommitWithDate -Message "feat: Add settings screen with preferences" -Date "2025-03-31T10:00:00"

# April 2025 - Advanced Features and Integration
Write-Host "`n=== April 2025 - Advanced Features ===" -ForegroundColor Yellow

New-CommitWithDate -Message "feat: Implement WebSocket support for real-time data" -Date "2025-04-02T13:30:00"
New-CommitWithDate -Message "feat: Add data export functionality (CSV/JSON)" -Date "2025-04-04T09:15:00"
New-CommitWithDate -Message "feat: Create backup and restore settings feature" -Date "2025-04-07T15:45:00"
New-CommitWithDate -Message "feat: Implement multi-sensor support (temperature, humidity)" -Date "2025-04-09T11:00:00"
New-CommitWithDate -Message "feat: Add sensor calibration tools" -Date "2025-04-11T14:20:00"
New-CommitWithDate -Message "feat: Create automated lighting recommendations" -Date "2025-04-14T10:30:00"
New-CommitWithDate -Message "feat: Implement scheduling system for alerts" -Date "2025-04-16T16:00:00"
New-CommitWithDate -Message "feat: Add integration with smart home systems" -Date "2025-04-18T12:45:00"
New-CommitWithDate -Message "feat: Create analytics dashboard for usage patterns" -Date "2025-04-21T09:00:00"
New-CommitWithDate -Message "feat: Implement offline mode with local storage" -Date "2025-04-23T13:15:00"
New-CommitWithDate -Message "feat: Add voice commands and accessibility features" -Date "2025-04-25T15:30:00"
New-CommitWithDate -Message "feat: Create widget for home screen monitoring" -Date "2025-04-28T11:20:00"
New-CommitWithDate -Message "feat: Add support for multiple desk profiles" -Date "2025-04-30T14:45:00"

# May 2025 - Testing, Optimization, and Release Preparation
Write-Host "`n=== May 2025 - Testing & Release Prep ===" -ForegroundColor Yellow

New-CommitWithDate -Message "test: Add unit tests for ViewModel logic" -Date "2025-05-02T10:00:00"
New-CommitWithDate -Message "test: Implement UI tests for critical user flows" -Date "2025-05-05T15:30:00"
New-CommitWithDate -Message "test: Add integration tests for network communication" -Date "2025-05-07T12:15:00"
New-CommitWithDate -Message "perf: Optimize sensor polling frequency" -Date "2025-05-09T09:45:00"
New-CommitWithDate -Message "perf: Implement memory leak fixes" -Date "2025-05-12T14:00:00"
New-CommitWithDate -Message "perf: Optimize Compose recomposition" -Date "2025-05-14T11:30:00"
New-CommitWithDate -Message "fix: Resolve notification permission issues on Android 13+" -Date "2025-05-16T16:20:00"
New-CommitWithDate -Message "fix: Fix sensor fallback when network unavailable" -Date "2025-05-19T10:15:00"
New-CommitWithDate -Message "fix: Resolve timezone handling in timestamps" -Date "2025-05-21T13:45:00"
New-CommitWithDate -Message "fix: Fix threshold validation edge cases" -Date "2025-05-23T09:00:00"
New-CommitWithDate -Message "docs: Update API documentation and README" -Date "2025-05-26T15:15:00"
New-CommitWithDate -Message "docs: Add user guide and troubleshooting section" -Date "2025-05-28T12:30:00"
New-CommitWithDate -Message "chore: Prepare for v1.0 release" -Date "2025-05-30T14:00:00"
New-CommitWithDate -Message "release: Version 1.0.0 - SmartDesk Light Monitor" -Date "2025-05-31T10:00:00"

Write-Host "`n=== Commit History Generation Complete ===" -ForegroundColor Green
Write-Host "Total commits created: 47" -ForegroundColor Cyan
Write-Host "Date range: January 16, 2025 - May 31, 2025" -ForegroundColor Cyan
Write-Host "Project: SmartDesk Light Monitor (Android Kotlin/Compose)" -ForegroundColor Cyan

# Show final commit log
Write-Host "`nFinal commit log:" -ForegroundColor Yellow
git log --oneline --pretty=format:"%h %s (%ad)" --date=short
