# PowerShell script to generate daily commits from June 24 to December 18, 2025

# Configuration
$repoPath = "$(Get-Location)"
$startDate = [DateTime]::Parse("2025-06-24")
$endDate = [DateTime]::Parse("2025-12-18")

# Git user configuration
$gitConfig = @{
    "user.name" = "Pandi Harshan"
    "user.email" = "pandiharshan@example.com"
}

# Apply Git config
Set-Location $repoPath
foreach ($key in $gitConfig.Keys) {
    git config $key $gitConfig[$key]
}

# Function to generate realistic commit messages based on day and progress
function Get-CommitMessage {
    param (
        [DateTime]$date,
        [int]$dayOfYear,
        [int]$totalDays
    )
    
    $dayOfWeek = $date.DayOfWeek
    $isWeekend = $dayOfWeek -eq "Saturday" -or $dayOfWeek -eq "Sunday"
    $progress = [math]::Round(($dayOfYear / $totalDays) * 100)
    
    # Different types of commit messages
    $featureWork = @(
        "feat: add new $($featureComponents | Get-Random) component",
        "feat: implement $($featureParts | Get-Random) functionality",
        "feat: enhance $($uiComponents | Get-Random) with $($enhancements | Get-Random)",
        "feat: create $($newComponents | Get-Random) for better user experience"
    )
    
    $bugFixes = @(
        "fix: resolve issue with $($buggyComponents | Get-Random) in $($problemAreas | Get-Random)",
        "fix: correct $($errorTypes | Get-Random) in $($components | Get-Random)",
        "fix: patch security vulnerability in $($securityAreas | Get-Random)",
        "fix: address $($bugTypes | Get-Random) reported by $($reporters | Get-Random)"
    )
    
    $refactors = @(
        "refactor: improve $($components | Get-Random) $($refactorActions | Get-Random)",
        "refactor: optimize $($performanceAreas | Get-Random) for better performance",
        "refactor: clean up $($codeAreas | Get-Random) code",
        "refactor: reorganize $($structureParts | Get-Random) structure"
    )
    
    $docs = @(
        "docs: update $($docTypes | Get-Random) documentation",
        "docs: add examples for $($exampleTopics | Get-Random)",
        "docs: fix typos in $($docSections | Get-Random) section",
        "docs: improve API reference for $($apiComponents | Get-Random)"
    )
    
    $chores = @(
        "chore: update dependencies",
        "chore: configure $($configFiles | Get-Random)",
        "chore: set up $($tooling | Get-Random) tooling",
        "chore: clean up build process"
    )
    
    # Define message parts
    $components = @("navbar", "footer", "sidebar", "dashboard", "homepage", "user profile", "settings", "authentication", "API", "database")
    $featureComponents = @("responsive", "interactive", "dynamic", "reusable", "modular")
    $featureParts = @("user authentication", "data fetching", "form validation", "state management", "API integration")
    $uiComponents = @("buttons", "forms", "modals", "tables", "navigation", "carousel", "cards", "alerts")
    $enhancements = @("animations", "accessibility", "performance", "responsiveness", "user feedback")
    $newComponents = @("header component", "footer section", "dashboard widget", "settings panel", "notification system")
    
    $buggyComponents = @("form submission", "data loading", "state updates", "API calls", "rendering")
    $problemAreas = @("mobile view", "dark mode", "form validation", "data synchronization", "performance")
    $errorTypes = @("type error", "race condition", "memory leak", "UI glitch", "styling issue")
    $securityAreas = @("authentication", "data validation", "API endpoints", "user sessions", "file uploads")
    $bugTypes = @("UI bug", "performance issue", "race condition", "edge case", "compatibility problem")
    $reporters = @("QA team", "users", "error monitoring", "performance metrics", "security scan")
    
    $refactorActions = @("code structure", "naming conventions", "error handling", "state management", "component hierarchy")
    $performanceAreas = @("rendering", "data fetching", "state updates", "asset loading", "build process")
    $codeAreas = @("utility functions", "component logic", "API layer", "state management", "test files")
    $structureParts = @("project", "module", "component", "API", "test")
    
    $docTypes = @("README", "API", "component", "setup", "deployment")
    $exampleTopics = @("usage", "configuration", "customization", "integration", "troubleshooting")
    $docSections = @("getting started", "API reference", "examples", "FAQ", "troubleshooting")
    $apiComponents = @("endpoints", "authentication", "query parameters", "response formats", "error handling")
    
    $configFiles = @("webpack", "babel", "eslint", "prettier", "jest", "typescript")
    $tooling = @("testing", "linting", "formatting", "bundling", "deployment")
    
    # Weighted random selection based on day and progress
    $rand = Get-Random -Minimum 1 -Maximum 100
    
    # Early project: More setup and initial features
    if ($progress -lt 25) {
        if ($rand -lt 40) { return $featureWork | Get-Random }
        if ($rand -lt 60) { return $chores | Get-Random }
        if ($rand -lt 80) { return $docs | Get-Random }
        return $refactors | Get-Random
    }
    # Mid project: Mix of features and fixes
    elseif ($progress -lt 75) {
        if ($rand -lt 30) { return $featureWork | Get-Random }
        if ($rand -lt 55) { return $bugFixes | Get-Random }
        if ($rand -lt 75) { return $refactors | Get-Random }
        if ($rand -lt 90) { return $docs | Get-Random }
        return $chores | Get-Random
    }
    # Late project: More bug fixes and polish
    else {
        if ($rand -lt 40) { return $bugFixes | Get-Random }
        if ($rand -lt 65) { return $refactors | Get-Random }
        if ($rand -lt 85) { return $docs | Get-Random }
        return $chores | Get-Random
    }
}

# Initialize arrays for commit patterns
$commitPatterns = @()
$totalDays = ($endDate - $startDate).Days

# Generate commit patterns for each day
for ($i = 0; $i -le $totalDays; $i++) {
    $currentDate = $startDate.AddDays($i)
    $dayOfWeek = $currentDate.DayOfWeek
    $isWeekend = $dayOfWeek -eq "Saturday" -or $dayOfWeek -eq "Sunday"
    
    # Skip some days randomly (less likely on weekdays)
    if ($isWeekend) {
        $skipDay = (Get-Random -Minimum 0 -Maximum 100) -lt 50
    } else {
        $skipDay = (Get-Random -Minimum 0 -Maximum 100) -lt 20
    }
    if ($skipDay) { continue }
    
    # Determine number of commits (1-3 on weekdays, 0-2 on weekends)
    if ($isWeekend) {
        $commitCount = Get-Random -Minimum 0 -Maximum 3
    } else {
        $commitCount = Get-Random -Minimum 1 -Maximum 4
    }
    
    for ($j = 0; $j -lt $commitCount; $j++) {
        if ($isWeekend) {
            $hour = Get-Random -Minimum 9 -Maximum 18
        } else {
            $hour = Get-Random -Minimum 8 -Maximum 20
        }
        $minute = Get-Random -Minimum 0 -Maximum 60
        $second = Get-Random -Minimum 0 -Maximum 60
        
        $commitDate = [DateTime]::new(
            $currentDate.Year,
            $currentDate.Month,
            $currentDate.Day,
            $hour, $minute, $second
        )
        
        $commitPatterns += @{
            Date = $commitDate
            DayOfYear = $i
        }
    }
}

# Create initial commit if needed
if (-not (git rev-parse --verify HEAD 2>$null)) {
    $initialCommitDate = $startDate.AddHours(10).ToString("yyyy-MM-ddTHH:mm:ss+05:30")
    $env:GIT_AUTHOR_DATE = $initialCommitDate
    $env:GIT_COMMITTER_DATE = $initialCommitDate
    git add .
    git commit -m "Initial commit: Project setup"
    Remove-Item Env:\GIT_AUTHOR_DATE
    Remove-Item Env:\GIT_COMMITTER_DATE
}

# Process each commit
foreach ($commit in $commitPatterns) {
    $dateStr = $commit.Date.ToString("yyyy-MM-ddTHH:mm:ss+05:30")
    $message = Get-CommitMessage -date $commit.Date -dayOfYear $commit.DayOfYear -totalDays $totalDays
    
    # Make a small change to a file
    $content = "Last updated: $($commit.Date.ToString('yyyy-MM-dd HH:mm:ss')) - $message"
    Add-Content -Path "$repoPath\commit_log.txt" -Value $content
    
    # Stage and commit with specific date
    git add "commit_log.txt"
    
    $env:GIT_AUTHOR_DATE = $dateStr
    $env:GIT_COMMITTER_DATE = $dateStr
    
    try {
        git commit -m $message
        Write-Host "[$($commit.Date.ToString('yyyy-MM-dd'))] $message" -ForegroundColor Green
    } catch {
        Write-Host "No changes to commit for $dateStr" -ForegroundColor Yellow
    }
    
    # Clean up environment variables
    Remove-Item Env:\GIT_AUTHOR_DATE
    Remove-Item Env:\GIT_COMMITTER_DATE
}

# Final message
Write-Host "`n=== Commit Generation Complete ===" -ForegroundColor Green
Write-Host "Total commits created: $(git rev-list --count HEAD)" -ForegroundColor Cyan
Write-Host "`nTo push to GitHub, run:" -ForegroundColor Cyan
Write-Host "git remote add origin https://github.com/Pandiharshan/Snowman.git" -ForegroundColor Yellow
Write-Host "git branch -M main" -ForegroundColor Yellow
Write-Host "git push -u origin main" -ForegroundColor Yellow
