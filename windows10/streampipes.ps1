if ($args.Count -lt 1)
{
    Write-Host "Use 'streampipes start' to start StreamPipes, 'streampipes stop' to stop it, and 'streampipes clean' to remove streampipes."
}

$env:COMPOSE_CONVERT_WINDOWS_PATHS = 1
$currentDir = Split-Path -parent $MyInvocation.MyCommand.Definition
$envFileTemp = $currentDir + "/../templates/.env"
$envFileDest = $currentDir + "/.env"
$dockerComposeFullTemp = $currentDir + "/../templates/docker-compose.full.yml"
$dockerComposeLiteTemp = $currentDir + "/../templates/docker-compose.lite.yml"
$dockerCompose = $currentDir + "/docker-compose.yml"
$configFolder = $currentDir + "/config"

function Show-Menu
{     
	 Write-Host "StreamPipes can be started in two different setups:"
	 Write-Host ""
     Write-Host "1: StreamPipes Lite (few pipeline elements, needs less memory)"
     Write-Host "2: StreamPipes Full (more pipeline elements, requires > 16 GB RAM)"
     Write-Host "Q: Press 'Q' to quit."
}


if ($args[0] -eq "start")
{
	
	Write-Host ' _______ __                              ______ __                    '
	Write-Host '|     __|  |_.----.-----.---.-.--------.|   __ \__|.-----.-----.-----.'
	Write-Host '|__     |   _|   _|  -__|  _  |        ||    __/  ||  _  |  -__|__ --|'
	Write-Host '|_______|____|__| |_____|___._|__|__|__||___|  |__||   __|_____|_____|'
	Write-Host '                                                   |__|'
	Write-Host ''
	Write-Host 'Welcome to StreamPipes!'
	Write-Host ''
	
	if (!(Test-Path  ($dockerCompose)))
	{
		do
		{
			 Show-Menu
			 $input = Read-Host "Please make a selection"
			 switch ($input)
			 {
				   '1' {
						$version="lite"
						break
						
				   } '2' {
						$version="full"
						break
				   }
			 }
			 pause
		}
		until (($input -eq '1') -Or ($input -eq '2'))
		
		Copy-Item $envFileTemp -Destination $envFileDest
		if ($version -eq "lite")
		{
			Copy-Item $dockerComposeLiteTemp -Destination $dockerCompose
		}
		if ($version -eq "full")
		{
			Copy-Item $dockerComposeFullTemp -Destination $dockerCompose
		}
		if ($args[1] -eq "")
		{
			$ip = (Get-NetIPConfiguration | Where-Object { $_.IPv4DefaultGateway -ne $null -and $_.NetAdapter.Status -ne "Disconnected" }).IPv4Address.IPAddress
		} 
		else 
		{
			$ip = $args[1]
		}
		(Get-Content $envFileDest).replace('SP_HOST=', 'SP_HOST=' +$ip) | Set-Content $envFileDest
		(Get-Content $envFileDest).replace('SP_KAFKA_HOST=', 'SP_KAFKA_HOST=' +$ip) | Set-Content $envFileDest
	} 	


    Invoke-Expression "docker-compose -f $dockerCompose pull"
    Invoke-Expression "docker-compose -f $dockerCompose up -d"

    if ($LASTEXITCODE -eq 0)
    {
        Write-Host "StreamPipes successfully started. Open browser and navigate to 'localhost"
    }
    else
    {
        Write-Host "ERROR: There was a problem while starting StreamPipes"
    }
}

if ($args[0] -eq "stop" -Or $args[0] -eq "clean")
{
	if ($args[0] -eq "stop")
    {
    Invoke-Expression "docker-compose -f $dockerCompose stop"
    Invoke-Expression "echo y | docker-compose -f $dockerCompose down"

    Write-Host "StreamPipes successfully stopped"
	}

    if ($args[0] -eq "clean")
    {
		Invoke-Expression "echo y | docker-compose -f $dockerCompose down -v"
        Remove-Item $envFileDest
        Remove-Item $dockerCompose

        Write-Host "All StreamPipes system information was deleted. The system can now be installed again."
    }
}


