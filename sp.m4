#!/bin/bash

# ARG_OPTIONAL_SINGLE([hostname],, [Set the default hostname of your server by providing the IP or DNS name], )
# ARG_OPTIONAL_BOOLEAN([defaultip],d, [When set the first ip is used as default])
# ARG_OPTIONAL_BOOLEAN([all],a, [Select all available StreamPipes services])
# ARG_POSITIONAL_MULTI([operation], [The StreamPipes operation (operation-name) (service-name (optional))], 3, [], [])
# ARG_TYPE_GROUP_SET([operation], [type string], [operation], [start,stop,restart,update,set-template,log,list-available,list-active,list-templates,activate,add,deactivate,clean,remove-settings,generate-compose-file,set-env,unset-env,create-compose])
# ARG_DEFAULTS_POS
# ARG_HELP([This script provides advanced features to run StreamPipes on your server])
# ARG_VERSION([echo This is the StreamPipes dev installer v0.1])
# ARGBASH_SET_INDENT([  ])
# ARGBASH_GO

# [ <-- needed because of Argbash

run() {

#	if [ $_arg_logs = "on" ]; 
#	then
		$1
#	else
#		$1 > /dev/null 2>&1
#	fi
}

endEcho() {
	echo ''
	echo $1
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
        echo 'Default IP was selected: '${ip}
    else 

				if [ $_arg_defaultip = "on" ]; 
				then
        	ip=${allips[0]}
        	echo 'Default IP was selected: '${ip}
				else
					echo ''
					echo 'Please select your IP address or add one manually: '
					PS3='Select option: '
					select opt in "${allips[@]}"
					do
							if [ -z "${opt}" ];
							then 
									echo "Wrong input, select one of the options"; 
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

createCompose() {
  IFS=''

	result=$(head -n 28 docker-compose.yml)

	while read service; do
  	result=$result"\n \n"$(tail -n +3 "services/$service/docker-compose.yml")
	done <system

	result=$result"\n \n"$(tail -n -13 docker-compose.yml)

	echo -e "$result" > "docker-compose.yml-generated"

	echo "New compose file is generated: docker-compose.tml-generated"
}

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
  if [ -e ./templates/"$1" ]; then
		cp ./templates/$1 system
	  echo "Set configuration for $1" 
	else
		echo "Configuration $1 was not found"
	fi
}


getCommand() {
    command="docker-compose -f docker-compose.yml"
    while IFS='' read -r line || [[ -n "$line" ]]; do
        command="$command -f ./services/$line/docker-compose.yml"
    done < "./system"
}

startStreamPipes() {

	if [ ! -f "./system" ]; 
	then
		moveSystemConfig system
	fi

	if [ ! -f "./.env" ] || [ $_arg_defaultip = "on" ]; 
    then
		getIp
		sed "s/##IP##/${ip}/g" ./tmpl_env > .env
	fi
    getCommand
		echo "Starting StreamPipes ${_arg_operation[1]}"
    run "$command up -d ${_arg_operation[1]}"

    endEcho "StreamPipes started ${_arg_operation[1]}"
}

updateStreamPipes() {
    getCommand

		echo "Updating StreamPipes ${_arg_operation[1]}"
    run "$command up -d ${_arg_operation[1]}"

		endEcho "Services updated"
}

updateServices() {
    getCommand
    $command pull ${_arg_operation[1]}

    endEcho "Service updated. Execute sp restart ${_arg_operation[1]} to restart service"
}

stopStreamPipes() {
    getCommand

		echo "Stopping StreamPipes ${_arg_operation[1]}"
    if [ "${_arg_operation[1]}" = "" ]; 
		then
    	run "$command down"
		else
    	run "$command stop ${_arg_operation[1]}"
    	run "$command rm -f ${_arg_operation[1]}"
		fi

    endEcho "StreamPipes stopped ${_arg_operation[1]}"
}

restartStreamPipes() {
	getCommand
	echo "Restarting StreamPipes."
	run "$command restart ${_arg_operation[1]}"

  endEcho "StreamPipes restarted ${_arg_operation[1]}"

}

logServices() {
    getCommand
    $command logs ${_arg_operation[1]}
}

cleanStreamPipes() {
    stopStreamPipes
    rm -r ./config
    endEcho "All configurations of StreamPipes have been deleted."
}

removeStreamPipesSettings() {
    stopStreamPipes
		rm .env
}

resetStreamPipes() {
    cleanStreamPipes
    rm .env
    echo "All configurations of StreamPipes have been deleted."
}

listAvailableServices() {
	echo "Available services:"
  cd services
  for dir in */ ; do
  	echo $dir | sed "s/\///g" 
  done
  cd ..
}

listActiveServices() {
	echo "Active services:"
	cat system
}

listTemplates() {
	echo "Available Templates:"
  cd templates
  for file in * ; do
  	echo $file 
  done
	cd ..
}


deactivateService() {
    if [ "$_arg_all" = "on" ]; 
    then
        removeAllServices
    else
        if grep -iq "${_arg_operation[1]}" system;then 
            sed -i "/${_arg_operation[1]}/d" ./system
            echo "Service ${_arg_operation[1]} removed"
            else
            echo "Service ${_arg_operation[1]} is currently not running"
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
        if grep -iq "${_arg_operation[1]}" system;then 
            echo "Service ${_arg_operation[1]} already exists"
        else
            echo ${_arg_operation[1]} >> ./system
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
            echo "Service $service_name already exists"
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

if [ "$_arg_operation" = "log" ];
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

if [ "$_arg_operation" = "create-compose" ];
then
   createCompose 
fi

if [ "$_arg_operation" = "nil" ];
then
    print_help
fi

# ] <-- needed because of Argbash
