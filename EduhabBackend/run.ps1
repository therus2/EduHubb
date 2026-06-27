param(
    [switch]$SkipDb
)

$ErrorActionPreference = "Stop"
$JDK11 = "C:\Users\rusna\.jdks\jbr_dcevm-11.0.16"
$MVN_STANDALONE = "C:\tools\maven\apache-maven-3.8.8\bin\mvn.cmd"
$MVN_IDEA = "C:\MyProgram\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd"

if (Test-Path $MVN_STANDALONE) {
    $MVN = $MVN_STANDALONE
    Write-Host "      Using standalone Maven: $MVN" -ForegroundColor DarkGray
} elseif (Test-Path $MVN_IDEA) {
    $MVN = $MVN_IDEA
    Write-Host "      Using IntelliJ Maven: $MVN" -ForegroundColor DarkGray
} else {
    Write-Error "Maven not found at:`n  $MVN_STANDALONE`n  $MVN_IDEA"
    exit 1
}

if (-not $SkipDb) {
    $null = docker info 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Docker is not running. Start Docker Desktop and try again, or use -SkipDb"
        exit 1
    }
    $pg = docker ps --filter "name=eduhub-db" --format "{{.Names}}" 2>$null
    if (-not $pg) {
        Write-Host "[1/3] Starting PostgreSQL..." -ForegroundColor Cyan
        docker compose up -d
        Write-Host "      Waiting for PostgreSQL to be ready..." -ForegroundColor Yellow
        do {
            Start-Sleep -Seconds 2
            $ready = docker exec eduhub-db pg_isready -U eduhub 2>$null
        } while (-not $ready)
        Write-Host "      PostgreSQL is ready" -ForegroundColor Green
    } else {
        Write-Host "[1/3] PostgreSQL already running" -ForegroundColor Green
    }
} else {
    Write-Host "[1/3] Skipping PostgreSQL (-SkipDb)" -ForegroundColor Yellow
}

$env:JAVA_HOME = $JDK11
$env:SPRING_PROFILES_ACTIVE = "prod"

$portOwner = Get-NetTCPConnection -LocalPort 8067 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($portOwner) {
    $oldPid = $portOwner.OwningProcess
    $oldProc = Get-Process -Id $oldPid -ErrorAction SilentlyContinue
    $procName = if ($oldProc) { $oldProc.ProcessName } else { "unknown" }
    Write-Host "[!] Port 8067 busy ($procName, PID $oldPid), stopping old instance..." -ForegroundColor Yellow
    Stop-Process -Id $oldPid -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}

Write-Host "[2/3] Starting EduHubBackend..." -ForegroundColor Cyan
$env:SERVER_PORT = "8067"
Write-Host "      API: http://localhost:8067/api/" -ForegroundColor White
Write-Host "[3/3] Press Ctrl+C to stop`n" -ForegroundColor Cyan

& $MVN spring-boot:run -f "D:\Development\SamsaProject\EduhabBackend"
