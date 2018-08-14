# CLI tool for StreamPipes

* Start StreamPipes with `streampipes start`
* Stop StreamPipes with `streampipes stop [start]`
* Clean StreamPipes with `streampipes clean [start]`
* Edit the file `system` to add or remove modules. Alternatively use the command `streampipes service add SERVICE_NAME` or `streampipes service remove SERVICE_NAME`

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