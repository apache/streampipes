## IoTDB

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Stores events in a IoTDB database.

***

## Required input

This sink does not have any requirements and works with any incoming event type.

***

## Configuration

### Hostname

The hostname of the IoTDB instance.

### Port

The port of the IoTDB instance (default 6667).

### Storage Group Name

The name of the storage group where events will be stored (will be created if it does not exist).
For each element of the stream a new time series will be created.

### Username

The username for the IoTDB Server.

### Password

The password for the IoTDB Server.

## Output

(not applicable for data sinks)
