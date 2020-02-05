#!/bin/bash

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

#!/bin/bash

# ARG_OPTIONAL_BOOLEAN([all],a, [Select all available StreamPipes services])
# ARG_POSITIONAL_MULTI([operation], [The StreamPipes operation (operation-name) (service-name (optional))], 3, [], [])
# ARG_TYPE_GROUP_SET([operation], [type string], [operation], [start,stop,restart,update,set-template,logs,list-available,list-active,list-templates,activate,add,deactivate,clean,force-clean,remove-settings,set-version])
# ARG_DEFAULTS_POS
# ARG_HELP([This script provides advanced features to run StreamPipes on your server])
# ARG_VERSION([echo This is the StreamPipes dev installer v0.1])
# ARGBASH_SET_INDENT([  ])
# ARGBASH_GO

# [ <-- needed because of Argbash

#SP_HOME=${SP_HOME:-/opt/streampipes}
curr_dir=$(pwd)
SP_HOME=${SP_HOME:-$curr_dir}

# --- helper functions for logs ---
info()
{
    echo "[INFO] " "$@"
}
warning()
{
    echo "[WARNING] " "$@"
}
fatal()
{
    echo "[ERROR] " "$@"
    exit 1
}

deployment_notice() {
	echo
	echo
	echo "INFO: StreamPipes CE $1 is now ready to be used on your system"
	echo "      Check https://streampipes.org/ for information on StreamPipes"
	echo
	echo "      Go to the UI and follow the instructions to get started: http://$2/"
	echo
}

run() {

$1
if [ $? -ne 0 ]; then
    fatal "Error occured while executing the StreamPipes command."
fi

}

getHostDockerInteralIP() {
  # get os type
  OS_TYPE=`uname`
  info "Detected OS: ${OS_TYPE}"
  if [ "$OS_TYPE" == "Linux" ]; then
    ip=$(ip -4 addr show docker0 | grep -Po 'inet \K[\d.]+')
    info "Detected Docker0 bridge IP: ${ip}"
  else
    # get IP of docker vm hosting docker for mac and docker windows
    ip=$(docker run --rm -it alpine getent hosts host.docker.internal | awk '{ print $1 }') > /dev/null 2>&1
    docker rmi alpine > /dev/null 2>&1
    info "Detected Docker Host IP: ${ip}"
  fi
}

moveSystemConfig() {
  if [ -e $SP_HOME/templates/"$1" ]; then
		cp $SP_HOME/templates/$1 $SP_HOME/system
	  info "Set configuration for template: $1"
	else
		info "Configuration $1 was not found"
	fi
}

createNetwork() {
	if [ ! "$(docker network ls | grep spnet)" ]; then
	  info "Creating StreamPipes network"
	  run "docker network create --driver=bridge --subnet=172.31.0.0/16 --gateway=172.31.0.1 spnet" > /dev/null 2>&1
	else
	  info "StreamPipes network already exists. Continuing"
	fi
}

setVersion() {
	# change pe version
	version=SP_PE_VERSION=${_arg_operation[1]}
	sed "s/SP_PE_VERSION=.*/${version}/g" ./tmpl_env > ./del_tmpl_env
	mv ./del_tmpl_env ./tmpl_env

	# change backend version
	version=SP_BACKEND_VERSION=${_arg_operation[1]}
	sed "s/SP_BACKEND_VERSION=.*/${version}/g" ./tmpl_env > ./del_tmpl_env
	mv ./del_tmpl_env ./tmpl_env

	info "Change StreamPipes version to ${_arg_operation[1]}"
}

getCommand() {
		command="env $(cat $SP_HOME/.env) docker-compose --project-name streampipes"
    while IFS='' read -r line || [[ -n "$line" ]]; do
        command="$command -f $SP_HOME/services/$line/docker-compose.yml"
    done < "$SP_HOME/system"
}

startStreamPipes() {

	if [ ! -f "$SP_HOME/system" ];
	then
		moveSystemConfig default
	fi

	if [ ! -f "$SP_HOME/.env" ];
    then
    info 'Initial StreamPipes configuration'
    getHostDockerInteralIP
    sed "s/##HOST_DOCKER_INTERNAL##/${ip}/g" $SP_HOME/tmpl_env > $SP_HOME/.env
	fi
    info "Starting StreamPipes ${_arg_operation[1]}"
		createNetwork
    getCommand
    run "$command up -d ${_arg_operation[1]}"

		SP_BACKEND_VERSION=`grep SP_BACKEND_VERSION "$SP_HOME/.env" | awk -F= '{print $2}'`

		deployment_notice $SP_BACKEND_VERSION "localhost"
}

updateStreamPipes() {
    getCommand

		info "Updating StreamPipes ${_arg_operation[1]}"
    run "$command streampipes up -d ${_arg_operation[1]}"

		info "StreamPipes services updated"
}

updateServices() {
    getCommand
    $command pull ${_arg_operation[1]}

    info "Service updated. Execute sp restart ${_arg_operation[1]} to restart service"
}

stopStreamPipes() {
    getCommand

		info "Stopping StreamPipes ${_arg_operation[1]}"
    if [ "${_arg_operation[1]}" = "" ];
		then
    	run "$command down"
			info "Removing StreamPipes network"
			run "docker network rm spnet" > /dev/null 2>&1
		else
    	run "$command stop ${_arg_operation[1]}"
    	run "$command rm -f ${_arg_operation[1]}"
		fi

    info "StreamPipes stopped ${_arg_operation[1]}"
}

restartStreamPipes() {
	getCommand
	info "Restarting StreamPipes."
	run "$command restart ${_arg_operation[1]}"

  info "StreamPipes restarted ${_arg_operation[1]}"

}

logServices() {
    getCommand
    $command logs ${_arg_operation[1]}
}

cleanStreamPipes() {
    if [ "$(docker ps --format '{{.Names}}' | grep streampipes)" ]; then
      fatal "Running StreamPipes services detected. Stop them before: 'sp stop'"
    else
      if [ "$(docker volume ls --filter name=streampipes -q)" ]; then
        info "Removing StreamPipes volumes"
        run "docker volume rm $(docker volume ls --filter name=streampipes -q)" > /dev/null 2>&1
      fi
      info "All StreamPipes configurations/data volumes were deleted."
    fi
}

forceCleanStreamPipes() {
    if [ "$(docker ps --format '{{.Names}}' | grep streampipes)" ]; then
      info "Running StreamPipes services detected and stopped"
      stopStreamPipes
      if [ "$(docker volume ls --filter name=streampipes -q)" ]; then
        info "Removing StreamPipes volumes"
        run "docker volume rm $(docker volume ls --filter name=streampipes -q)" > /dev/null 2>&1
      fi
      info "All StreamPipes configurations/data volumes were deleted."
    fi
}

removeStreamPipesSettings() {
    stopStreamPipes
		rm $SP_HOME/.env
}

resetStreamPipes() {
    cleanStreamPipes
    rm $SP_HOME/.env
    info "All configurations of StreamPipes have been deleted."
}

listAvailableServices() {
	info "Available StreamPipes services:"
  for dir in $SP_HOME/services/* ; do
  	#echo $dir | sed "s/\///g"
    echo ${dir##*/}
  done
}

listActiveServices() {
  if [ "$(docker ps --format '{{.Names}}' | grep streampipes | awk -F'_' '{print $2}')" ]; then
    info "Running StreamPipes services:"
    docker ps --format '{{.Names}}' | grep streampipes | awk -F'_' '{print $2}'
  else
    info "No StreamPipes services running. To start run 'sp start'"
  fi
}

listTemplates() {
  info "Available StreamPipes templates:"
  for file in $SP_HOME/templates/* ; do
  	echo ${file##*/}
  done
}

deactivateService() {
    if [ "$_arg_all" = "on" ];
    then
        removeAllServices
    else
        if grep -iq "${_arg_operation[1]}" $SP_HOME/system;then
            sed -i "/${_arg_operation[1]}/d" $SP_HOME/system
            info "Service ${_arg_operation[1]} removed"
            else
            info "Service ${_arg_operation[1]} is currently not running"
        fi
    fi
}

activateService() {
	addService
	updateStreamPipes
}

addService() {
    if [ "$_arg_all" = "on" ];
    then
        addAllServices
    else
        if grep -iq "${_arg_operation[1]}" $SP_HOME/system;then
            warning "StreamPipes service ${_arg_operation[1]} already exists"
        else
            echo ${_arg_operation[1]} >> $SP_HOME/system
        fi
    fi

}

removeAllServices() {
    stopStreamPipes
    > system
}

setTemplate() {
  moveSystemConfig ${_arg_operation[1]}
}

addAllServices() {
    cd services
    for dir in */ ; do
        service_name=`echo $dir | sed "s/\///g"`
        if grep -iq "$service_name" ../system;then
            warning "StreamPipes service $service_name already exists"
        else
            echo $service_name >> ../system
        fi
    done
    cd ..
    updateStreamPipes
}

export COMPOSE_CONVERT_WINDOWS_PATHS=1
cd "$(dirname "$0")"

if [ "$_arg_operation" = "start" ];
then
    startStreamPipes
fi

if [ "$_arg_operation" = "stop" ];
then
    stopStreamPipes
fi

if [ "$_arg_operation" = "restart" ];
then
    restartStreamPipes
fi

if [ "$_arg_operation" = "clean" ];
then
    cleanStreamPipes
fi

if [ "$_arg_operation" = "force-clean" ];
then
    forceCleanStreamPipes
fi

if [ "$_arg_operation" = "remove-settings" ];
then
    removeStreamPipesSettings
fi

if [ "$_arg_operation" = "activate" ];
then
    activateService
fi

if [ "$_arg_operation" = "add" ];
then
    addService
fi


if [ "$_arg_operation" = "deactivate" ];
then
    deactivateService
fi

if [ "$_arg_operation" = "list-available" ];
then
    listAvailableServices
fi

if [ "$_arg_operation" = "list-active" ];
then
    listActiveServices
fi

if [ "$_arg_operation" = "list-templates" ];
then
    listTemplates
fi

if [ "$_arg_operation" = "update" ];
then
    updateServices
fi

if [ "$_arg_operation" = "logs" ];
then
    logServices
fi

if [ "$_arg_operation" = "reset" ];
then
    resetStreamPipes
fi

if [ "$_arg_operation" = "set-template" ];
then
    setTemplate
fi


if [ "$_arg_operation" = "set-version" ];
then
   setVersion
fi

if [ "$_arg_operation" = "nil" ];
then
    print_help
fi

# ] <-- needed because of Argbash
