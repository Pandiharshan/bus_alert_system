# SmartDesk Light Monitor - Realistic Commit History Generator
# Generates human-like commit patterns from January 16 to May 31, 2025

Write-Host "Starting realistic commit history generation..." -ForegroundColor Green

# Git configuration
git config user.name "SmartDesk Developer"
git config user.email "dev@smartdesk.com"

# Reset existing history for clean generation
git reset --hard HEAD~1 2>$null

# Function to get random number within range
function Get-RandomInRange {
    param($min, $max)
    return [System.Random]::new().Next($min, $max + 1)
}

# Function to get random double
function Get-RandomDouble {
    param($max)
    return [System.Random]::new().NextDouble() * $max
}

# Function to check if date is weekend
function Test-IsWeekend {
    param([datetime]$date)
    return $date.DayOfWeek -eq 'Saturday' -or $date.DayOfWeek -eq 'Sunday'
}

# Function to check if date is holiday (simulate some holidays)
function Test-IsHoliday {
    param([datetime]$date)
    # Simulate some holidays
    $holidays = @(
        [datetime]"2025-01-20", # MLK Day
        [datetime]"2025-02-17", # Presidents Day
        [datetime]"2025-04-18", # Good Friday
        [datetime]"2025-05-26"  # Memorial Day
    )
    return $holidays -contains $date.Date
}

# Function to get random commit time during work hours
function Get-RandomWorkTime {
    param([datetime]$date)
    $hour = Get-RandomInRange 9 17  # 9 AM to 5 PM
    $minute = Get-RandomInRange 0 59
    return $date.Date.AddHours($hour).AddMinutes($minute)
}

# Function to create commits with realistic patterns
function New-RealisticCommit {
    param(
        [string]$Message,
        [datetime]$Date,
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
    $env:GIT_COMMITTER_DATE = $Date.ToString("yyyy-MM-ddTHH:mm:ss")
    $env:GIT_AUTHOR_DATE = $Date.ToString("yyyy-MM-ddTHH:mm:ss")
    git commit -m $Message
    $env:GIT_COMMITTER_DATE = ""
    $env:GIT_AUTHOR_DATE = ""
    
    Write-Host "Created commit: $Message ($Date)" -ForegroundColor Cyan
}

# Realistic commit messages based on project analysis
$commitMessages = @(
    # Feature commits
    "feat: add light sensor integration",
    "feat: implement real-time lux monitoring",
    "feat: create dashboard UI with Compose",
    "feat: add threshold configuration",
    "feat: implement alert notifications",
    "feat: add network client for external sensors",
    "feat: create sensor fallback mechanism",
    "feat: add preset configurations",
    "feat: implement data persistence",
    "feat: add Material 3 theme support",
    "feat: create responsive layouts",
    "feat: add animation transitions",
    "feat: implement WebSocket support",
    "feat: add data export functionality",
    "feat: create backup/restore feature",
    "feat: add multi-sensor support",
    "feat: implement calibration tools",
    "feat: add scheduling system",
    "feat: integrate smart home systems",
    "feat: create analytics dashboard",
    "feat: implement offline mode",
    "feat: add accessibility features",
    "feat: create home screen widget",
    "feat: add voice commands",
    "feat: implement multiple profiles",
    
    # Fix commits
    "fix: resolve sensor reading issues",
    "fix: fix notification permissions",
    "fix: resolve memory leaks",
    "fix: fix timezone handling",
    "fix: resolve connection timeouts",
    "fix: fix UI layout bugs",
    "fix: resolve crash on startup",
    "fix: fix data corruption issues",
    "fix: resolve performance bottlenecks",
    "fix: fix edge case handling",
    
    # Refactor commits
    "refactor: optimize sensor polling",
    "refactor: improve code organization",
    "refactor: enhance error handling",
    "refactor: optimize Compose performance",
    "refactor: improve network layer",
    "refactor: enhance data models",
    "refactor: improve test coverage",
    "refactor: optimize memory usage",
    "refactor: enhance UI components",
    "refactor: improve build process",
    
    # Test commits
    "test: add unit tests for ViewModel",
    "test: implement UI tests",
    "test: add integration tests",
    "test: improve test coverage",
    "test: add performance tests",
    "test: implement E2E tests",
    
    # Documentation commits
    "docs: update README",
    "docs: add API documentation",
    "docs: improve code comments",
    "docs: add user guide",
    "docs: update changelog",
    "docs: improve troubleshooting guide",
    
    # Chore commits
    "chore: update dependencies",
    "chore: upgrade Gradle version",
    "chore: configure CI/CD",
    "chore: improve build scripts",
    "chore: update lint rules",
    "chore: prepare for release",
    
    # Style commits
    "style: format code",
    "style: fix lint issues",
    "style: improve naming conventions",
    "style: enhance code readability"
)

# Development phases with realistic intensity
$developmentPhases = @(
    @{ Start = [datetime]"2025-01-16"; End = [datetime]"2025-01-31"; Intensity = "High";   Description = "Initial Setup" },
    @{ Start = [datetime]"2025-02-01"; End = [datetime]"2025-02-28"; Intensity = "High";   Description = "Core Development" },
    @{ Start = [datetime]"2025-03-01"; End = [datetime]"2025-03-31"; Intensity = "Medium"; Description = "UI Polish" },
    @{ Start = [datetime]"2025-04-01"; End = [datetime]"2025-04-30"; Intensity = "High";   Description = "Feature Sprint" },
    @{ Start = [datetime]"2025-05-01"; End = [datetime]"2025-05-31"; Intensity = "Medium"; Description = "Testing & Release" }
)

Write-Host "`n=== Generating Realistic Commit History ===" -ForegroundColor Yellow

$totalCommits = 0
$currentDate = [datetime]"2025-01-16"
$endDate = [datetime]"2025-05-31"

while ($currentDate -le $endDate) {
    # Skip weekends and holidays
    if (Test-IsWeekend $currentDate -or (Test-IsHoliday $currentDate)) {
        $currentDate = $currentDate.AddDays(1)
        continue
    }
    
    # Find current development phase
    $currentPhase = $developmentPhases | Where-Object { $_.Start -le $currentDate -and $_.End -ge $currentDate } | Select-Object -First 1
    
    # Determine commit probability based on phase intensity
    $commitProbability = switch ($currentPhase.Intensity) {
        "High"   { 0.85 }  # 85% chance of commits during high intensity
        "Medium" { 0.65 }  # 65% chance during medium intensity
        "Low"    { 0.35 }  # 35% chance during low intensity
        default  { 0.5 }
    }
    
    # Randomly decide if there are commits today
    if ((Get-RandomDouble 1) -lt $commitProbability) {
        # Determine number of commits for today (1-4 typically, occasional bursts)
        $numCommits = switch ($currentPhase.Intensity) {
            "High"   { if ((Get-RandomInRange 0 100) -lt 15) { Get-RandomInRange 5 8 } else { Get-RandomInRange 1 4 } }
            "Medium" { if ((Get-RandomInRange 0 100) -lt 10) { Get-RandomInRange 3 5 } else { Get-RandomInRange 1 3 } }
            "Low"    { Get-RandomInRange 1 2 }
            default  { Get-RandomInRange 1 2 }
        }
        
        # Create commits for today
        for ($i = 0; $i -lt $numCommits; $i++) {
            $commitTime = Get-RandomWorkTime $currentDate
            $randomMessage = $commitMessages | Get-Random
            New-RealisticCommit -Message $randomMessage -Date $commitTime
            $totalCommits++
            
            # Small delay between commits to simulate natural workflow
            Start-Sleep -Milliseconds 100
        }
        
        Write-Host "Day $($currentDate.ToString('yyyy-MM-dd')): $numCommits commits" -ForegroundColor Green
    } else {
        Write-Host "Day $($currentDate.ToString('yyyy-MM-dd')): No commits (rest day)" -ForegroundColor Gray
    }
    
    $currentDate = $currentDate.AddDays(1)
}

Write-Host "`n=== Realistic Commit History Generation Complete ===" -ForegroundColor Green
Write-Host "Total commits created: $totalCommits" -ForegroundColor Cyan
Write-Host "Date range: January 16, 2025 - May 31, 2025" -ForegroundColor Cyan
Write-Host "Weekends and holidays excluded" -ForegroundColor Cyan
Write-Host "Variable commit patterns with realistic intensity" -ForegroundColor Cyan

# Show final commit statistics
Write-Host "`nCommit statistics:" -ForegroundColor Yellow
$commitStats = git log --pretty=format:"%ad" --date=format:"%Y-%m-%d" | Group-Object | Sort-Object Name
Write-Host "Active development days: $($commitStats.Count)" -ForegroundColor Cyan
$avgCommits = [math]::Round($totalCommits / $commitStats.Count, 1)
Write-Host "Average commits per active day: $avgCommits" -ForegroundColor Cyan
