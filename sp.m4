#!/bin/bash

# ARG_OPTIONAL_SINGLE([hostname], , [The default hostname of your server], )
# ARG_OPTIONAL_BOOLEAN([prune],p, [Prune docker networks])
# ARG_OPTIONAL_BOOLEAN([clean],c, [Start from a clean StreamPipes session])
# ARG_OPTIONAL_BOOLEAN([current],u, [Show only currently registered services])
# ARG_OPTIONAL_BOOLEAN([all],a, [Select all available StreamPipes services])
# ARG_POSITIONAL_MULTI([operation], [The StreamPipes operation (operation-name) (service-name (optional))], 2, [], [])
# ARG_TYPE_GROUP_SET([operation], [type string], [operation], [test,start,stop,restart,clean,add,remove,update,list,logs,reset,set-template])
# ARG_DEFAULTS_POS
# ARG_HELP([This script provides advanced features to run StreamPipes on your server])
# ARG_VERSION([echo This is the StreamPipes dev installer v0.1])
# ARGBASH_SET_INDENT([  ])
# ARGBASH_GO

# [ <-- needed because of Argbash

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

}


getCommand() {
    command="docker-compose -f docker-compose.yml"
    while IFS='' read -r line || [[ -n "$line" ]]; do
        command="$command -f ./services/$line/docker-compose.yml"
    done < "./system"
}

startStreamPipes() {
    docker stop $(docker ps -a -q)
    docker network prune -f
    getIp
    sed "s/##IP##/${ip}/g" ./tmpl_env > .env
    getCommand
    $command up -d ${_arg_operation[1]}
    echo 'StreamPipes sucessfully started'
}

updateStreamPipes() {
    getCommand
    $command up -d ${_arg_operation[1]}
}

updateServices() {
    getCommand
    $command pull ${_arg_operation[1]}
    $command up -d ${_arg_operation[1]}
}

stopStreamPipes() {
    getCommand
    $command down 
    echo 'StreamPipes sucessfully stopped'
}

logServices() {
    getCommand
    $command logs ${_arg_operation[1]}
}

cleanStreamPipes() {
    stopStreamPipes
    rm -r ./config
    echo "All configurations of StreamPipes have been deleted."
}

resetStreamPipes() {
    cleanStreamPipes
    rm .env
    echo "All configurations of StreamPipes have been deleted."
}

listServices() {
    if [ "$_arg_current" = "on" ]; 
    then
        echo "Currently registered services:"
        cat system
    else 
        echo "Available services:"
        cd services
        for dir in */ ; do
          echo $dir | sed "s/\///g" 
        done
        cd ..
    fi
}

removeService() {
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

addService() {
    if [ "$_arg_all" = "on" ]; 
    then
        addAllServices
    else
        if grep -iq "${_arg_operation[1]}" system;then 
            echo "Service ${_arg_operation[1]} already exists"
        else
            echo ${_arg_operation[1]} >> ./system
            updateStreamPipes
        fi
    fi
    
}

removeAllServices() {
    stopStreamPipes
    > system
}

setTemplate() {
# bigdata|desktop|ui-developer|pe-developer|backend-developer
    echo "Set-Template is currently not yet implemented."
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
    stopStreamPipes
    startStreamPipes
    echo 'StreamPipes sucessfully restarted'

fi

if [ "$_arg_operation" = "clean" ];
then
    cleanStreamPipes
fi

if [ "$_arg_operation" = "add" ];
then
    addService
fi

if [ "$_arg_operation" = "remove" ];
then
    removeService
fi

if [ "$_arg_operation" = "cleanstart" ];
then
    cleanStreamPipes
    startStreamPipes
    echo 'All configurations of StreamPipes are deleted and StreamPipes is restarted'
fi

if [ "$_arg_operation" = "list" ];
then
    listServices
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

if [ "$_arg_operation" = "nil" ];
then
    print_help
fi

# ] <-- needed because of Argbash
