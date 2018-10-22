# CLI tool for StreamPipes

All active services are defined in the system file.
All available services are in the services folder.

## Features Suggestion
* start (service-name) (--hostname) (--prune) (--defaultip)
  * Starts StreamPipes.
* stop (service-name) 
  * Stops StreamPipes and deletes containers
* restart (service-name) 
  * Restarts containers
* update (service-name)
  * Updates and restarts docker container
* logs (service-name)
  * Prints the logs of the service

* activate {add} (service-name) (--all)
  * Adds service to system 
* deactivate {remove} (service-name) (--all // do we need this here?)
* list (--available) (--current)
  * Lists the names of the services

* clean
  * Stops and cleans SP installation, remove networks
* reset: Delete all .env files and everything else (Do we need this in adition to clean?)

* template (template-name:bigdata|desktop|ui-developer|pe-developer|backend-developer){NOT SUPPORTED YET} 

## Flags

* ARG_OPTIONAL_SINGLE([hostname], , [The default hostname of your server], )
* ARG_OPTIONAL_BOOLEAN([logs],l, [When set the first ip is used as default])
* ARG_OPTIONAL_BOOLEAN([defaultip],d, [When set the first ip is used as default])
* ARG_OPTIONAL_BOOLEAN([prune],p, [Prune docker networks])
 * Why is this a flag and not a command?
* ARG_OPTIONAL_BOOLEAN([clean],c, [Start from a clean StreamPipes session])
 * Why is this a flag and not a command?
* ARG_OPTIONAL_BOOLEAN([current],u, [Show only currently registered services])
* ARG_OPTIONAL_BOOLEAN([all],a, [Select all available StreamPipes services])
* ARG_POSITIONAL_MULTI([operation], [The StreamPipes operation (operation-name) (service-name (optional))], 2, [], [])



## Features

* start (service-name) (--all) (--hostname) (--prune)
* stop (service-name) (--all)
* restart (service-name) (--all)
* add (service-name) (--all)
* remove (service-name) (--all)
* update (service-name) (--all)
* list (--available) (--current)
* logs (service-name) (--all)
* clean: Clean SP installation, remove networks
* reset: Delete all .env files and everything else
* template (template-name:bigdata|desktop|ui-developer|pe-developer|backend-developer)


## Usage

~/argbash/argbash-2.7.0/bin/argbash sp.m4 -o sp
