$filePath = Join-Path $PSScriptRoot 'insert_cards_and_logs.ps1'
$content = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)
$utf8Bom = New-Object System.Text.UTF8Encoding($true)
[System.IO.File]::WriteAllText($filePath, $content, $utf8Bom)
Write-Host "BOM added"
