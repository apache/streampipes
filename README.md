# CLI tool for StreamPipes

All active services are defined in the system file.
All available services are in the services folder.

## Features Suggestion
* start (service-name) (--hostname "valueHostName") (--defaultip)
  * Starts StreamPipes or service
* stop (service-name) 
  * Stops StreamPipes and deletes containers
* restart (service-name) 
  * Restarts containers
* update (service-name) (--renew)
  * Downloads new docker images 
  * --renew restart containers after download
* set-template (template-name)
  * Replaces the systems file with file mode-name
* log (service-name)
  * Prints the logs of the service

* list-available
* list-active
* list-templates

* activate (service-name) (--all)
  * Adds service to system and starts
* add (service-name) (--all)
  * Adds service to system 
* deactivate {remove} (service-name)  (--all)
  * Stops container and removes from system file

* clean
  * Stops and cleans SP installation, remove networks
* remove-settings: 
  * Stops StreamPipes and deletes .env file

* generate-compose-file


## Flags

* ARG_OPTIONAL_SINGLE([hostname], , [The default hostname of your server], )
* ARG_OPTIONAL_BOOLEAN([defaultip],d, [When set the first ip is used as default])
* ARG_OPTIONAL_BOOLEAN([all],a, [Select all available StreamPipes services])


## Usage

~/argbash/argbash-2.7.0/bin/argbash sp.m4 -o sp


## Naming Files / Folders
* active-services
* services/
* system-configurations -> templates/
* tmpl_env
