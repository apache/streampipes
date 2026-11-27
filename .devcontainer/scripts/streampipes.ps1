# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

param(
    [Parameter(Mandatory, Position=0)][ValidateSet("start", "stop", "status")][string]$Action,
    [ValidateSet("all", "core", "extensions", "ui")][string]$Only = "all",
    [switch]$Build
)
$ErrorActionPreference = "Stop"
$devContainer = Split-Path -Parent $PSScriptRoot
$compose = @("compose", "-f", (Join-Path $devContainer "docker-compose.yml"))
$envFile = Join-Path $devContainer ".env"
if (Test-Path -LiteralPath $envFile) { $compose += @("--env-file", $envFile) }

$workspace = & docker @compose ps -q workspace
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
if (-not $workspace) { throw "Open this checkout in a Dev Container first." }
if (@($workspace).Count -ne 1) { throw "Expected one workspace for this Compose project." }
# Compose project names can be shared by two checkouts. Check the project file
# before starting dependencies or sending stop signals to the workspace.
$labels = & docker inspect --format '{{json .Config.Labels}}' $workspace
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$configFiles = ($labels | ConvertFrom-Json).'com.docker.compose.project.config_files'
if (($configFiles -split ',') -notcontains (Join-Path $devContainer "docker-compose.yml")) {
    # An IDE running through WSL can record the same checkout with a Linux path.
    $probeName = ".workspace-" + [guid]::NewGuid().ToString("N")
    $probe = Join-Path $devContainer $probeName
    [IO.File]::WriteAllText($probe, "")
    try {
        & docker exec $workspace test -f "/workspaces/streampipes/.devcontainer/$probeName"
        if ($LASTEXITCODE -ne 0) {
            throw "This Compose workspace belongs to another checkout. Set a unique COMPOSE_PROJECT_NAME in .devcontainer/.env and open this checkout in a Dev Container."
        }
    } finally {
        Remove-Item -LiteralPath $probe
    }
}
if ($Action -eq "start" -and $Only -ne "ui") {
    & docker @compose up -d --no-deps --no-recreate couchdb nats influxdb
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
$command = @("bash", ".devcontainer/scripts/services.sh", $Action, $Only)
if ($Build) { $command += "--build" }
& docker exec -u vscode -w /workspaces/streampipes $workspace @command
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
if ($Action -eq "start" -and $Only -in @("ui", "all")) {
    $mapping = & docker @compose port workspace 8082
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    if ($mapping -match ':(\d+)$') { Write-Host "Open http://localhost:$($Matches[1])" }
}
