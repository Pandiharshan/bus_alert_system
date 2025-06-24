#!/bin/bash

# Initialize variables
REPO_DIR="$(pwd)"
GIT_AUTHOR_NAME="Pandi Harshan"
GIT_AUTHOR_EMAIL="your.email@example.com"
GIT_COMMITTER_NAME="$GIT_AUTHOR_NAME"
GIT_COMMITTER_EMAIL="$GIT_AUTHOR_EMAIL"

# Set Git config for this repository
cd "$REPO_DIR"
git config user.name "$GIT_AUTHOR_NAME"
git config user.email "$GIT_AUTHOR_EMAIL"

# Function to create a commit with a specific date
commit_with_date() {
    local date_str="$1"
    local message="$2"
    local files="${3:-.}"
    
    # Only proceed if there are changes to commit
    if [ -z "$(git status --porcelain $files)" ]; then
        echo "No changes to commit for: $message"
        return
    fi
    
    # Add files to staging
    git add $files
    
    # Create commit with specific date
    GIT_AUTHOR_DATE="$date_str" \
    GIT_COMMITTER_DATE="$date_str" \
    git commit -m "$message"
}

# Initial commit (June 24, 2025)
commit_with_date "2025-06-24T10:00:00+05:30" "chore: initialize project with Create React App"

# Phase 1: Foundation (June 24 - July 10, 2025)
commit_with_date "2025-06-25T11:30:00+05:30" "feat: set up React Router and basic routing"
commit_with_date "2025-06-28T15:45:00+05:30" "feat: create core component structure"
commit_with_date "2025-07-02T09:15:00+05:30" "style: add global CSS reset and base styles"
commit_with_date "2025-07-05T14:20:00+05:30" "feat: implement responsive layout components"
commit_with_date "2025-07-10T16:30:00+05:30" "docs: update README with project structure"

# Phase 2: Core Components (July 11 - August 5, 2025)
commit_with_date "2025-07-15T10:30:00+05:30" "feat: implement About section"
commit_with_date "2025-07-20T11:45:00+05:30" "feat: add Features grid with animations"
commit_with_date "2025-07-25T14:15:00+05:30" "feat: implement Contact form with validation"
commit_with_date "2025-07-30T16:00:00+05:30" "style: enhance UI with consistent theming"
commit_with_date "2025-08-03T10:20:00+05:30" "feat: improve accessibility with ARIA attributes"

# Phase 3: Snowfall Animation (August 6 - August 25, 2025)
commit_with_date "2025-08-08T13:45:00+05:30" "feat: implement canvas-based snowfall animation"
commit_with_date "2025-08-15T15:30:00+05:30" "perf: optimize snowfall animation performance"
commit_with_date "2025-08-20T11:15:00+05:30" "fix: handle window resize events in snowfall"
commit_with_date "2025-08-25T14:50:00+05:30" "refactor: improve snowfall particle system"

# Phase 4: Theme System (August 26 - September 15, 2025)
commit_with_date "2025-08-30T10:30:00+05:30" "feat: add dark/light theme toggle"
commit_with_date "2025-09-05T11:45:00+05:30" "feat: implement system preference detection"
commit_with_date "2025-09-10T14:20:00+05:30" "feat: add theme persistence with localStorage"
commit_with_date "2025-09-15T16:10:00+05:30" "style: add smooth theme transition animations"

# Phase 5: 3D Integration (September 16 - October 10, 2025)
commit_with_date "2025-09-20T10:15:00+05:30" "feat: integrate Three.js and React Three Fiber"
commit_with_date "2025-09-25T14:30:00+05:30" "feat: add 3D Snowman model"
commit_with_date "2025-10-01T11:20:00+05:30" "feat: implement interactive camera controls"
commit_with_date "2025-10-10T15:45:00+05:30" "perf: optimize 3D rendering performance"

# Phase 6: Backend Development (October 11 - November 5, 2025)
commit_with_date "2025-10-15T10:30:00+05:30" "feat: set up Express server"
commit_with_date "2025-10-20T14:15:00+05:30" "feat: implement REST API endpoints"
commit_with_date "2025-10-25T11:45:00+05:30" "feat: add CORS and middleware configuration"
commit_with_date "2025-11-01T16:20:00+05:30" "feat: implement error handling middleware"
commit_with_date "2025-11-05T10:45:00+05:30" "docs: update API documentation"

# Phase 7: Polish & Optimization (November 6 - December 18, 2025)
commit_with_date "2025-11-10T13:30:00+05:30" "perf: implement lazy loading for route components"
commit_with_date "2025-11-20T15:15:00+05:30" "style: enhance UI with subtle animations"
commit_with_date "2025-11-30T11:30:00+05:30" "fix: resolve mobile responsiveness issues"
commit_with_date "2025-12-05T14:45:00+05:30" "test: add unit and integration tests"
commit_with_date "2025-12-10T10:20:00+05:30" "chore: update dependencies"
commit_with_date "2025-12-15T16:30:00+05:30" "docs: update README with deployment instructions"
commit_with_date "2025-12-18T12:00:00+05:30" "chore: final code cleanup and optimizations"

# Push all branches to remote
# Note: This will require authentication
echo "\nTo push to remote, run:"
echo "git push -f origin main"
