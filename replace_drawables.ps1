# PowerShell script to replace PNG drawable references with vector drawable references

$replacements = @{
    "R.drawable.ic_arrow_back" = "R.drawable.ic_arrow_back_vector"
    "R.drawable.ic_directions_bus" = "R.drawable.ic_directions_bus_vector"
    "R.drawable.ic_notifications" = "R.drawable.ic_notifications"
    "R.drawable.ic_settings" = "R.drawable.ic_settings"
    "R.drawable.ic_chevron_right" = "R.drawable.ic_chevron_right"
    "R.drawable.ic_home" = "R.drawable.ic_home"
    "R.drawable.ic_person" = "R.drawable.ic_person"
    "R.drawable.ic_map" = "R.drawable.ic_map"
    "R.drawable.ic_qr_code_scanner" = "R.drawable.ic_qr_code_scanner"
    "R.drawable.ic_history" = "R.drawable.ic_history"
    "R.drawable.ic_calendar_month" = "R.drawable.ic_calendar_month"
    "R.drawable.ic_pending" = "R.drawable.ic_pending"
    "R.drawable.ic_check_circle" = "R.drawable.ic_check_circle"
    "R.drawable.ic_speed" = "R.drawable.ic_speed"
    "R.drawable.ic_pin_drop" = "R.drawable.ic_pin_drop"
    "R.drawable.ic_chat" = "R.drawable.ic_chat"
}

# Get all Kotlin files
$kotlinFiles = Get-ChildItem -Path "app/src/main/java" -Filter "*.kt" -Recurse

foreach ($file in $kotlinFiles) {
    $content = Get-Content $file.FullName -Raw
    $modified = $false
    
    foreach ($old in $replacements.Keys) {
        $new = $replacements[$old]
        if ($content -match [regex]::Escape($old)) {
            $content = $content -replace [regex]::Escape($old), $new
            $modified = $true
        }
    }
    
    if ($modified) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
        Write-Host "Updated: $($file.FullName)"
    }
}

Write-Host "Drawable replacement completed!"