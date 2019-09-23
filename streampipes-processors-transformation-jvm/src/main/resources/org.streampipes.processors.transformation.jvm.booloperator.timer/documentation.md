## Boolean Timer

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor measures how long a boolean value does not change. Once the value is changes the event with the measured time is emitted.


***

## Required input

A boolean value is required in the data stream.

### Field

The boolean field which is monitored for state changes.

***

## Configuration

### Timer value
Define whether it should be measured how long the value is true or how long the value is false.

## Output
Appends a field with the time how long the value did not change. Is emitted on the change of the boolean value. Runtime name: measured_time 
