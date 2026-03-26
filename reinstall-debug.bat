@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
if not exist "%ADB%" (
    echo 未找到 adb：%ADB%
    echo 请在 Android Studio - SDK Manager 中安装 Android SDK Platform-Tools。
    exit /b 1
)

echo 检查设备（请先启动模拟器或连接真机）...
"%ADB%" devices
"%ADB%" wait-for-device

echo 卸载 com.example.tx_ku ...
"%ADB%" uninstall com.example.tx_ku
echo （若提示 Failure 可能本来未安装，可忽略）

echo 安装 Debug ...
call gradlew.bat installDebug --no-daemon
if errorlevel 1 exit /b 1
echo 完成。
pause
