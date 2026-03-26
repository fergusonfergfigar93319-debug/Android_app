# 先启动 Android 模拟器（或连接真机并开启 USB 调试），再在本机 PowerShell 执行：
#   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force; .\reinstall-debug.ps1
#
# 作用：卸载 com.example.tx_ku 的 Debug 包并重新安装（等同 Clean 安装，避免旧 dex 残留）

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

$sdkDir = $null
$lp = Join-Path $root "local.properties"
if (Test-Path $lp) {
    Get-Content $lp | ForEach-Object {
        if ($_ -match '^\s*sdk\.dir\s*=\s*(.+)\s*$') {
            $sdkDir = $matches[1].Trim().Replace('\\', '\')
        }
    }
}
if (-not $sdkDir) { $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdkDir "platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    Write-Error "未找到 adb：$adb 。请在 Android Studio 的 SDK Manager 中安装 Platform-Tools。"
}

Write-Host "等待设备连接（请已启动模拟器或连接真机）..."
& $adb wait-for-device
$pkg = "com.example.tx_ku"
Write-Host "正在卸载 $pkg ..."
& $adb uninstall $pkg 2>$null
# 未安装时 uninstall 返回失败，忽略
Write-Host "正在编译并安装 Debug ..."
& (Join-Path $root "gradlew.bat") "installDebug" "--no-daemon"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "完成。请在模拟器上打开应用验证。"
