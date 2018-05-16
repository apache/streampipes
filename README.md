# Install StreamPipes
Clone this project or download the folder for your operating system.

## Linux and OSX
* Open the terminal and navigate to the directory `streampipes-installer/osx_linux`
* Execute `chmod +x streampipes`to make it executable
* Start StreamPipes with the commands in the next section

### Commands
Run the script with `./streampipes <COMMAND>`. We provide three different commands

* `./streampipes start`:
  * Downloads all Components and starts StreamPipes (optimized for Laptops with less then 16 GB RAM)
* `./streampipes start bigdata`:
  * Downloads all Components and starts StreamPipes (16GB of RAM recommended)
* `./streampipes stop`:
  * Stops StreamPipes
* `./streampipes clean`:
  * Removes all configuration files. This must be done when your computer has a new IP address.

## Windows 10
* Open the Command-Line / PowerShell and navigate to the directory `streampipes-installer/windows10`
* Start StreamPipes with the commands in the next section

### Commands
Run the script with `./streampipes.bat <COMMAND>`. We provide three different commands

* `./streampipes.bat start`:
  * Downloads all Components and starts StreamPipes (optimized for Laptops with less then 16 GB RAM, less algorithms)
* `./streampipes.bat start bigdata`:
  * Downloads all Components and starts StreamPipes (16 GB of RAM recommended)
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
 
