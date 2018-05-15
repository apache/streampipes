# Install StreamPipes
Clone this project or download the file `streampipes`

## Linux and OSX
* Open the terminal and navigate to the directory
* Execute `chmod +x streampipes`to make it executable
* Start StreamPipes with the commands in the next section

### Commands
Run the script with `./streampipes <COMMAND>`. We provide three different commands

* `./streampipes start`:
  * Downloads all Components and starts StreamPipes
* `./streampipes stop`:
  * Stops StreamPipes
* `./streampipes clean`:
  * Removes all configuration files. This must be done when your computer has a new IP address.

## Windows 10 (Experimental)
* Open the Command-Line / PowerShell and navigate to the directory
* Start StreamPipes with the commands in the next section

### Commands
Run the script with `./streampipes.bat <COMMAND>`. We provide three different commands

* `./streampipes.bat start`:
  * Downloads all Components and starts StreamPipes
* `./streampipes.bat stop`:
  * Stops StreamPipes
* `./streampipes.bat clean`:
  * Removes all configuration files. This must be done when your computer has a new IP address.
  
### Optional Commands

* `./streampipes.bat start bigdata`:
  * Downloads all Components and starts StreamPipes using the bigdata setup (includes Flink, Flink-based pipeline elements, Elasticsearch and Kibana)
  * Use one of the following commands: `bigdata` or `desktop`
  * If none is specified, the system will default to `desktop`
* `./streampipes.bat start bigdata HOSTNAME`:
  * Specify the hostname manually
  * If hostname is not set, the installer determines your current IP address.  
 