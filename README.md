# Install StreamPipes
Clone this project or download the complete repository. To download the ZIP of the installer click on the green button `Clone or download`on the top right. Then click on `download ZIP`.

A complete installation guide can be found at [https://www.streampipes.org/en/download](https://www.streampipes.org/en/download)

## Linux and OSX
* Open the terminal and navigate to the directory `streampipes-installer/osx_linux`
* Execute `chmod +x streampipes`to make it executable
* Start StreamPipes with the commands in the next section
* After starting, navigate to `localhost` in your Browser

### Commands
Run the script with `./streampipes <COMMAND>`. We provide three different commands:

* `./streampipes start`:
  * Downloads all Components and starts StreamPipes.
  * StreamPipes is a modular, easily extensible system consisting of several micro services. The installer automatically downloads all required services. Depending on your internet connection, the first installation may take some while.
* `./streampipes stop`:
  * Stops StreamPipes
* `./streampipes clean`:
  * Removes all configuration files. This must be done when your computer has a new IP address.

## Windows 10
* Open the Command-Line / PowerShell and navigate to the directory `streampipes-installer/windows10`
* Start StreamPipes with the commands in the next section
* After starting navigate to `localhost` in your Browser

### Commands
Run the script with `./streampipes.bat <COMMAND>`. We provide three different commands

* `./streampipes.bat start`:
  * Downloads all Components and starts StreamPipes
   * StreamPipes is a modular, easily extensible system consisting of several micro services. The installer automatically downloads all required services. Depending on your internet connection, the first installation may take some while.
* `./streampipes.bat stop`:
  * Stops StreamPipes
* `./streampipes.bat clean`:
  * Removes all configuration files. This must be done when your computer has a new IP address.
  
 
