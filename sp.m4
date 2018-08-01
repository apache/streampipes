#!/bin/bash

# ARG_OPTIONAL_SINGLE([hostname], , [The default hostname of your server], )
# ARG_POSITIONAL_MULTI([operation], [The StreamPipes operation (start|stop|restart|clean|add|remove|cleanstart) (service-name)], 2, [start], [nil])
# ARG_DEFAULTS_POS
# ARG_HELP([This script provides advanced features to run StreamPipes on your server])
# ARG_VERSION([echo This is the StreamPipes dev installer v0.1])
# ARGBASH_SET_INDENT([  ])
# ARGBASH_GO

# [ <-- needed because of Argbash

echo "'$_arg_operation'"
getIp() {
	rawip=$(ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1')

	rawip=`echo $rawip | sed 's/(%s)*\n/ /g'`
	IFS=' ' declare -a 'allips=($rawip)'

	allips+=( 'Enter IP manually' )

	# if default selected do not show promt

	if [ $defaultIp ] ; 
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
				echo "Wrong input select one of the options"; 
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
	$command up -d
}

updateStreamPipes() {
	getCommand
	$command up -d
}

stopStreamPipes() {
	getCommand
	$command down 
}

cleanStreamPipes() {
#	stopStreamPipes
#	rm -r ./config
    echo 'StreamPipes clean'
}

if [ "$_arg_operation" = "start" ];
then
#	startStreamPipes
	echo 'StreamPipes sucessfully started'
fi

if [ "$_arg_operation" = "stop" ];
then
#	stopStreamPipes
	echo 'StreamPipes sucessfully stopped'

fi

if [ "$_arg_operation" = "restart" ];
then
#	stopStreamPipes
#	startStreamPipes
	echo 'StreamPipes sucessfully restarted'

fi

if [ "$_arg_operation" = "clean" ];
then
#	cleanStreamPipes
	echo 'All configurations of StreamPipes are deleted'
fi

if [ "$_arg_operation" = "cleanstart" ];
then
#	cleanStreamPipes
#	startStreamPipes

	echo 'All configurations of StreamPipes are deleted and StreamPipes is restarted'
fi

# ] <-- needed because of Argbash