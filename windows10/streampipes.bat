@echo off

if [%1]==[] (
    echo Use 'streampipes start' to start StreamPipes, 'streampipes stop' to stop it, and 'streampipes clean' to remove streampipes.
    exit /b
)

SET ThisScriptsDirectory=%~dp0
SET PowerShellScriptPath=%ThisScriptsDirectory%streampipes.ps1

if "%1"=="start" (
if "%2"=="" (
        PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' 'start' 'desktop' ''";
    )
    if "%2"=="desktop" (
        if "%3"=="" (
			PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' 'start' 'desktop' ''";
		)
		if not "%3"=="" (
		echo Using %3
			PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' 'start' 'desktop' '%3'";
		)
    )
    if "%2"=="bigdata" (
        if "%3"=="" (
			PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' 'start' 'bigdata' ''";
		)
		if not "%3"=="" (
			PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' 'start' 'desktop' '%3'";
		)
    )
)

if "%1"=="stop" (
    PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' 'stop'";
)

if "%1"=="clean" (
    PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' 'clean'";
)