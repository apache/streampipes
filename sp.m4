#!/bin/bash

# ARG_OPTIONAL_SINGLE([hostname],, [Set the default hostname of your server by providing the IP or DNS name], )
# ARG_OPTIONAL_BOOLEAN([defaultip],d, [When set the first ip is used as default])
# ARG_OPTIONAL_BOOLEAN([all],a, [Select all available StreamPipes services])
# ARG_POSITIONAL_MULTI([operation], [The StreamPipes operation (operation-name) (service-name (optional))], 3, [], [])
# ARG_TYPE_GROUP_SET([operation], [type string], [operation], [start,stop,restart,update,set-template,log,list-available,list-active,list-templates,activate,add,deactivate,clean,remove-settings,set-env,unset-env,create-compose,set-version])
# ARG_DEFAULTS_POS
# ARG_HELP([This script provides advanced features to run StreamPipes on your server])
# ARG_VERSION([echo This is the StreamPipes dev installer v0.1])
# ARGBASH_SET_INDENT([  ])
# ARGBASH_GO

# [ <-- needed because of Argbash

SP_HOME=${SP_HOME:-/opt/streampipes}

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
	echo "  INFO: StreamPipes CE $1 is now ready to be used on your system"
	echo "        Check https://streampipes.org/ for information on StreamPipes"
	echo
	echo "        Go to the UI and follow the instructions to get started: http://$2/"
	echo
}

run() {

$1
if [ $? -ne 0 ]; then
    fatal "Error occured while executing the StreamPipes command."
fi

#	if [ $_arg_logs = "on" ];
#	then

#	else
#		$1 > /dev/null 2>&1
#	fi
}

endEcho() {
	echo ''
	info $1
}

getIp() {
    if [ -x "$(command -v ifconfig)" ]; then
        rawip=$(ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1')
    elif [ -x "$(command -v ipconfig)" ]; then
        rawip=$(ipconfig | grep -Eo 'IPv4.*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1')
    fi

    rawip=`echo $rawip | sed 's/(%s)*\n/ /g'`
    IFS=' ' declare -a 'allips=($rawip)'

    allips+=( 'Enter IP manually' )

    # if default selected do not show prompt

    if [ $_arg_hostname ] ;
    then
        ip=$_arg_hostname
        info 'Default IP was selected: '${ip}
    else

				if [ $_arg_defaultip = "on" ];
				then
        	ip=${allips[0]}
        	info 'Default IP was selected: '${ip}
				else
					echo ''
					info 'Please select your IP address or add one manually: '
					PS3='Select option: '
					select opt in "${allips[@]}"
					do
							if [ -z "${opt}" ];
							then
									warning "Wrong input, select one of the options";
							else
									ip="$opt"

									if [ "$opt" == "Enter IP manually" ];
									then
											read -p "Enter Ip: " ip
									fi
									break
							fi
        	done
				fi
    fi

}

#TODO: adapt to new format
# createCompose() {
#   IFS=''
# 	# Get consul
# 	#result=$(head -n 28 docker-compose.yml)
#
# 	while read service; do
#   	result=$result"\n \n"$(tail -n +3 "services/$service/docker-compose.yml" | sed '/spnet:/q')
# 	done <system
#
# 	# Generate network
# 	#result=$result"\n \n"$(tail -n -13 docker-compose.yml)
#
# 	echo -e "$result" > "docker-compose.yml-generated"
#
# 	info "New compose file is generated: docker-compose.tml-generated"
# }

unsetEnv() {
	cd services
	for dir in */ ; do
  file="$dir"docker-compose.yml

    one=${_arg_operation[1]}"="
	  two=${_arg_operation[2]}

	  result="$one$two"

	  IFS=''

    while read a ; do echo ${a//      - $one*/#      - $one} ; done < $file > ./$file.t ; mv $file{.t,}
	done
	cd ..
}

setEnv() {
	cd services
	for dir in */ ; do
  	file="$dir"docker-compose.yml

    one=${_arg_operation[1]}"="
	  two=${_arg_operation[2]}

	  result="$one$two"

	  IFS=''

	  while read a ; do echo ${a//#      - $one/      - $result} ; done < $file > ./$file.t ; mv $file{.t,}

	done
	cd ..
}

moveSystemConfig() {
  if [ -e $SP_HOME/streampipes-cli/templates/"$1" ]; then
		cp $SP_HOME/streampipes-cli/templates/$1 $SP_HOME/streampipes-cli/system
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

	echo "Change StreamPipes version to ${_arg_operation[1]}"
}

getCommand() {
		command="env $(cat $SP_HOME/streampipes-cli/.env) docker-compose --project-name streampipes"
    while IFS='' read -r line || [[ -n "$line" ]]; do
        command="$command -f $SP_HOME/streampipes-cli/services/$line/docker-compose.yml"
    done < "$SP_HOME/streampipes-cli/system"
}

startStreamPipes() {

	if [ ! -f "$SP_HOME/streampipes-cli/system" ];
	then
		moveSystemConfig default
	fi

	if [ ! -f "$SP_HOME/streampipes-cli/.env" ] || [ $_arg_defaultip = "on" ];
    then
		getIp
		sed "s/##IP##/${ip}/g" $SP_HOME/streampipes-cli/tmpl_env > $SP_HOME/streampipes-cli/.env
	fi
    info "Starting StreamPipes ${_arg_operation[1]}"
		createNetwork
    getCommand
    run "$command up -d ${_arg_operation[1]}"

		SP_BACKEND_VERSION=`grep SP_BACKEND_VERSION "$SP_HOME/streampipes-cli/.env" | awk -F= '{print $2}'`
		SP_HOST=`grep SP_HOST "$SP_HOME/streampipes-cli/.env" | awk -F= '{print $2}'`

    #endEcho "StreamPipes started ${_arg_operation[1]}"
		deployment_notice $SP_BACKEND_VERSION $SP_HOST
}

updateStreamPipes() {
    getCommand

		info "Updating StreamPipes ${_arg_operation[1]}"
    run "$command streampipes up -d ${_arg_operation[1]}"

		endEcho "Services updated"
}

updateServices() {
    getCommand
    $command pull ${_arg_operation[1]}

    endEcho "Service updated. Execute sp restart ${_arg_operation[1]} to restart service"
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

    endEcho "StreamPipes stopped ${_arg_operation[1]}"
}

restartStreamPipes() {
	getCommand
	info "Restarting StreamPipes."
	run "$command restart ${_arg_operation[1]}"

  endEcho "StreamPipes restarted ${_arg_operation[1]}"

}

logServices() {
    getCommand
    $command logs ${_arg_operation[1]}
}

cleanStreamPipes() {
    # if `docker ps --format '{{.Names}}' | grep streampipes`; then
    #   stopStreamPipes
    # fi
    #stopStreamPipes

    if [ "$(docker volume ls --filter name=streampipes -q)" ]; then
      info "Removing StreamPipes volumes"
      run "docker volume rm $(docker volume ls --filter name=streampipes -q)" > /dev/null 2>&1
    fi
    endEcho "All configurations of StreamPipes have been deleted."
}

removeStreamPipesSettings() {
    stopStreamPipes
		rm $SP_HOME/streampipes-cli/.env
}

resetStreamPipes() {
    cleanStreamPipes
    rm $SP_HOME/streampipes-cli/.env
    info "All configurations of StreamPipes have been deleted."
}

listAvailableServices() {
	info "Available StreamPipes services:"
  for dir in $SP_HOME/streampipes-cli/services/* ; do
  	#echo $dir | sed "s/\///g"
    echo ${dir##*/}
  done
}

listActiveServices() {
	info "Running StreamPipes services:"
	#cat $SP_HOME/streampipes-cli/system
  docker ps --format '{{.Names}}' | grep streampipes | awk -F'_' '{print $2}'
}

listTemplates() {
  info "Available StreamPipes templates:"
  for file in $SP_HOME/streampipes-cli/templates/* ; do
  	echo ${file##*/}
  done
}

deactivateService() {
    if [ "$_arg_all" = "on" ];
    then
        removeAllServices
    else
        if grep -iq "${_arg_operation[1]}" $SP_HOME/streampipes-cli/system;then
            sed -i "/${_arg_operation[1]}/d" $SP_HOME/streampipes-cli/system
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
        if grep -iq "${_arg_operation[1]}" $SP_HOME/streampipes-cli/system;then
            info "Service ${_arg_operation[1]} already exists"
        else
            echo ${_arg_operation[1]} >> $SP_HOME/streampipes-cli/system
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
            warning "Service $service_name already exists"
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

if [ "$_arg_operation" = "set-env" ];
then
    setEnv
fi

# if [ "$_arg_operation" = "create-compose" ];
# then
#    createCompose
# fi

if [ "$_arg_operation" = "set-version" ];
then
   setVersion
fi

if [ "$_arg_operation" = "nil" ];
then
    print_help
fi

# ] <-- needed because of Argbash
