# PowerShell script to generate Git commits with specific dates

# Set Git config for this repository
$repoPath = "$(Get-Location)"
$gitConfig = @{
    "user.name" = "Pandi Harshan"
    "user.email" = "your.email@example.com"
}

# Apply Git config
foreach ($key in $gitConfig.Keys) {
    git config $key $gitConfig[$key]
}

# Function to create a commit with a specific date
function New-GitCommit {
    param (
        [string]$Date,
        [string]$Message,
        [string]$Files = "."
    )
    
    # Check for changes
    $changes = git status --porcelain $Files
    if (-not $changes) {
        Write-Host "No changes to commit for: $Message" -ForegroundColor Yellow
        return
    }
    
    # Add files to staging
    git add $Files
    
    # Create commit with specific date
    $env:GIT_AUTHOR_DATE = $Date
    $env:GIT_COMMITTER_DATE = $Date
    git commit -m $Message
    
    # Clear the environment variables
    Remove-Item Env:\GIT_AUTHOR_DATE
    Remove-Item Env:\GIT_COMMITTER_DATE
}

# Function to create a series of commits for a phase
function New-DevelopmentPhase {
    param (
        [string]$PhaseName,
        [array]$Commits,
        [string]$StartDate,
        [int]$DaysBetweenCommits = 3
    )
    
    Write-Host "`n=== $PhaseName ===" -ForegroundColor Cyan
    $currentDate = [DateTime]::ParseExact($StartDate, "yyyy-MM-dd", $null)
    
    foreach ($commit in $Commits) {
        $commitDate = $currentDate.ToString("yyyy-MM-ddTHH:mm:ss+05:30")
        Write-Host "[$($currentDate.ToString('yyyy-MM-dd'))] $($commit)" -ForegroundColor Green
        
        New-GitCommit -Date $commitDate -Message $commit
        
        # Move to next date
        $currentDate = $currentDate.AddDays((Get-Random -Minimum 1 -Maximum ($DaysBetweenCommits + 1)))
    }
}

# Phase 1: Foundation (June 24 - July 10, 2025)
$phase1Commits = @(
    "chore: initialize project with Create React App",
    "feat: set up React Router and basic routing",
    "feat: create core component structure",
    "style: add global CSS reset and base styles",
    "feat: implement responsive layout components",
    "docs: update README with project structure"
)

# Phase 2: Core Components (July 11 - August 5, 2025)
$phase2Commits = @(
    "feat: implement About section",
    "feat: add Features grid with animations",
    "feat: implement Contact form with validation",
    "style: enhance UI with consistent theming",
    "feat: improve accessibility with ARIA attributes"
)

# Phase 3: Snowfall Animation (August 6 - August 25, 2025)
$phase3Commits = @(
    "feat: implement canvas-based snowfall animation",
    "perf: optimize snowfall animation performance",
    "fix: handle window resize events in snowfall",
    "refactor: improve snowfall particle system"
)

# Phase 4: Theme System (August 26 - September 15, 2025)
$phase4Commits = @(
    "feat: add dark/light theme toggle",
    "feat: implement system preference detection",
    "feat: add theme persistence with localStorage",
    "style: add smooth theme transition animations"
)

# Phase 5: 3D Integration (September 16 - October 10, 2025)
$phase5Commits = @(
    "feat: integrate Three.js and React Three Fiber",
    "feat: add 3D Snowman model",
    "feat: implement interactive camera controls",
    "perf: optimize 3D rendering performance"
)

# Phase 6: Backend Development (October 11 - November 5, 2025)
$phase6Commits = @(
    "feat: set up Express server",
    "feat: implement REST API endpoints",
    "feat: add CORS and middleware configuration",
    "feat: implement error handling middleware",
    "docs: update API documentation"
)

# Phase 7: Polish & Optimization (November 6 - December 18, 2025)
$phase7Commits = @(
    "perf: implement lazy loading for route components",
    "style: enhance UI with subtle animations",
    "fix: resolve mobile responsiveness issues",
    "test: add unit and integration tests",
    "chore: update dependencies",
    "docs: update README with deployment instructions",
    "chore: final code cleanup and optimizations"
)

# Execute the commit generation
New-DevelopmentPhase -PhaseName "Phase 1: Foundation (June 24 - July 10, 2025)" -Commits $phase1Commits -StartDate "2025-06-24"
New-DevelopmentPhase -PhaseName "Phase 2: Core Components (July 11 - August 5, 2025)" -Commits $phase2Commits -StartDate "2025-07-11"
New-DevelopmentPhase -PhaseName "Phase 3: Snowfall Animation (August 6 - August 25, 2025)" -Commits $phase3Commits -StartDate "2025-08-06"
New-DevelopmentPhase -PhaseName "Phase 4: Theme System (August 26 - September 15, 2025)" -Commits $phase4Commits -StartDate "2025-08-26"
New-DevelopmentPhase -PhaseName "Phase 5: 3D Integration (September 16 - October 10, 2025)" -Commits $phase5Commits -StartDate "2025-09-16"
New-DevelopmentPhase -PhaseName "Phase 6: Backend Development (October 11 - November 5, 2025)" -Commits $phase6Commits -StartDate "2025-10-11"
New-DevelopmentPhase -PhaseName "Phase 7: Polish & Optimization (November 6 - December 18, 2025)" -Commits $phase7Commits -StartDate "2025-11-06"

# Final message
Write-Host "`n=== Commit Generation Complete ===" -ForegroundColor Green
Write-Host "To push to remote, run:" -ForegroundColor Cyan
Write-Host "git push -f origin main" -ForegroundColor Yellow
