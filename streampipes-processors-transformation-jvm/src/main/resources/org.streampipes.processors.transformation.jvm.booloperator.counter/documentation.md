## Boolean Counter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor monitors a boolean value and counts how often the value of the boolean changes. 
A user can configure whether the changes from FALSE to TRUE, TRUE to FALSE, or BOTH changes should be counted.

***

## Required input

A boolean value is required in the data stream and can be selected with the field mapping.

### Boolean Field

The boolean value to be monitored.

***

## Configuration

A user can configure whether the changes from TRUE to FALSE, FALSE to TRUE, or all changes of the boolean value should be counted.

### Flank parameter

Either:
* TRUE -> FALSE: Increase counter on a true followed by a false 
* FALSE -> TRUE: Increase counter on a false followed by a true
* BOTH: Increas counter on each change of the boolean value on two consecutive events

## Output

Adds an additional numerical field with the current count value to the event. Events are just emitted when the counter changes.
Runtime Name: countField