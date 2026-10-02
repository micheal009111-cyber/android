@echo off
setlocal
set GRADLE_VERSION=8.7
set GRADLE_HOME=%USERPROFILE%\.gradle\pusher-gradle\gradle-%GRADLE_VERSION%
set GRADLE_ZIP=%USERPROFILE%\.gradle\pusher-gradle\gradle-%GRADLE_VERSION%-bin.zip

if exist "%GRADLE_HOME%\bin\gradle.bat" goto run
if not exist "%USERPROFILE%\.gradle\pusher-gradle" mkdir "%USERPROFILE%\.gradle\pusher-gradle"
if not exist "%GRADLE_ZIP%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%GRADLE_ZIP%'"
)
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%GRADLE_ZIP%' '%USERPROFILE%\.gradle\pusher-gradle'"

:run
call "%GRADLE_HOME%\bin\gradle.bat" %*
endlocal
