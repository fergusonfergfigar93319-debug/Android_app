# Repair API 34 Google Play x86_64 system image (clean partial + single sdkmanager).
# Run: powershell -NoProfile -ExecutionPolicy Bypass -File .\repair-sdk-api34.ps1

$ErrorActionPreference = "Stop"
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$javaHome = "${env:ProgramFiles}\Android\Android Studio\jbr"
$sdkmanager = "$sdk\cmdline-tools\latest\bin\sdkmanager.bat"
$pkg = "system-images;android-34;google_apis_playstore;x86_64"

Write-Host "Stopping emulator / adb (ignore errors)"
cmd /c "taskkill /F /IM qemu-system-x86_64.exe 2>nul"
cmd /c "taskkill /F /IM qemu-system-x86.exe 2>nul"
cmd /c "taskkill /F /IM adb.exe 2>nul"

Write-Host "Stopping SdkManagerCli only"
Get-CimInstance Win32_Process -Filter "Name='java.exe'" -ErrorAction SilentlyContinue |
    Where-Object { $_.CommandLine -like "*SdkManagerCli*" } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }

Write-Host "Cleaning temp and broken image folder"
Remove-Item -Path "$sdk\.temp\PackageOperation*" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "$sdk\.downloadIntermediates\*" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "$sdk\system-images\android-34\google_apis_playstore\x86_64" -Recurse -Force -ErrorAction SilentlyContinue

if (-not (Test-Path $sdkmanager)) {
    Write-Error "sdkmanager not found: $sdkmanager"
}

$env:JAVA_HOME = $javaHome
Write-Host "Installing $pkg (~1.5GB, do not run twice)"
& $sdkmanager --sdk_root=$sdk --install $pkg
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Installed (verify line below):"
& $sdkmanager --sdk_root=$sdk --list_installed | Select-String "android-34;google_apis_playstore"
Write-Host "Done."
