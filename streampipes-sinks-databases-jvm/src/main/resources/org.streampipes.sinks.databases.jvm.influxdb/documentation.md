## InfluxDB

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Stores events in an InfluxDB.

***

## Required input

This sink requires an event that provides a timestamp value (a field that is marked to be of type ``http://schema
.org/DateTime``.

***

## Configuration

### Hostname

The hostname/URL of the InfluxDB instance. (Include http(s)://).

### Port

The port of the InfluxDB instance.

### Database Name

The name of the database where events will be stored.

### Measurement Name

The name of the Measurement where events will be stored (will be created if it does not exist).

### Username

The username for the InfluxDB Server.

### Password

The password for the InfluxDB Server.

### Timestamp Field

The field which contains the required timestamp.

### Buffer Size

Indicates how many events are written into a buffer, before they are written to the database.

### Maximum Flush

The maximum waiting time for the buffer to fill the Buffer size before it will be written to the database in ms.
## Output

(not applicable for data sinks)
