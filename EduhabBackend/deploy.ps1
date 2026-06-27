$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

if (-not (Test-Path ".env") -and (Test-Path ".env.example")) {
    Copy-Item .env.example .env
}

$null = docker info 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Error "Docker is not running."
    exit 1
}

Write-Host "Starting PostgreSQL + backend (port 8067)..." -ForegroundColor Cyan
docker compose up -d --build
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$port = 8067
if (Test-Path ".env") {
    Get-Content .env | ForEach-Object {
        if ($_ -match '^\s*BACKEND_PORT\s*=\s*(\d+)\s*$') { $port = [int]$Matches[1] }
    }
}

Write-Host "Waiting for API on port $port..." -ForegroundColor Yellow
for ($i = 1; $i -le 40; $i++) {
    try {
        $r = Invoke-WebRequest -Uri "http://127.0.0.1:$port/api/user" -UseBasicParsing -TimeoutSec 3
        if ($r.StatusCode -eq 200) {
            Write-Host "Ready: http://localhost:$port/api/" -ForegroundColor Green
            docker compose ps
            exit 0
        }
    } catch {}
    Start-Sleep -Seconds 3
}

Write-Host "Containers up. Check logs: docker compose logs -f backend" -ForegroundColor Yellow
docker compose ps
