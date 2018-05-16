if ($args.Count -lt 1)
{
    Write-Host "Use 'streampipes start' to start StreamPipes, 'streampipes stop' to stop it, and 'streampipes clean' to remove streampipes."
}

$env:COMPOSE_CONVERT_WINDOWS_PATHS = 1
$currentDir = Split-Path -parent $MyInvocation.MyCommand.Definition
$dockerComposeTemp = $currentDir + "/tmpl-docker-compose.yml"
$dockerCompose = $currentDir + "/docker-compose.yml"
$configFolder = $currentDir + "/config"

if ($args[0] -eq "start")
{
    if ($args[1] -eq "desktop")
    {
        Invoke-RestMethod -Uri "https://raw.githubusercontent.com/streampipes/preview-docker/master/desktop/docker-compose.yml" -OutFile $dockerComposeTemp
    }
    if ($args[1] -eq "bigdata")
    {
        Invoke-RestMethod -Uri "https://raw.githubusercontent.com/streampipes/preview-docker/master/big-data/docker-compose.yml" -OutFile $dockerComposeTemp
    }

    $ip = (Get-NetIPConfiguration | Where-Object { $_.IPv4DefaultGateway -ne $null -and $_.NetAdapter.Status -ne "Disconnected" }).IPv4Address.IPAddress
    (Get-Content $dockerComposeTemp).replace('###TODO ADD HOSTNAME HERE ###', $ip) | Set-Content $dockerCompose

    Invoke-Expression "docker-compose -f $dockerCompose stop"
    Invoke-Expression "echo y | docker-compose -f $dockerCompose down -v"

    Invoke-Expression "docker-compose -f $dockerCompose pull"
    Invoke-Expression "docker-compose -f $dockerCompose up -d"

    if ($LASTEXITCODE -eq 0)
    {
        Write-Host "StreamPipes successfully started"
    }
    else
    {
        Write-Host "ERROR: There was a problem while starting StreamPipes"
    }
}

if ($args[0] -eq "stop" -Or $args[0] -eq "clean")
{
    Invoke-Expression "docker-compose -f $dockerCompose stop"
    Invoke-Expression "echo y | docker-compose -f $dockerCompose down -v"

    Write-Host "StreamPipes successfully stopped"

    if ($args[0] -eq "clean")
    {
        Remove-Item $dockerComposeTemp
        Remove-Item $dockerCompose
        Remove-Item -Recurse -Force $configFolder

        Write-Host "All StreamPipes System informations were deleted. The system can now be installed again."
    }
}
