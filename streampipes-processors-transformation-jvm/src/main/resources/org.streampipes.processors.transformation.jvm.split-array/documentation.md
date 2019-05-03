## Split Array

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor takes an array of event properties and creates an event for each of them. Further property of the events can be added to each element
Add a detailed description here

***

## Required input

This processor works with any event that has a field of type ``list``.

***

## Configuration

### Keep Fields

Fields of the event that should be kept in each resulting event.

### List field

The name of the field that contains the list values that should be split.


## Output

This data processor produces an event with all fields selected by the ``Keep Fields`` parameter and all fields of the
 selected list field.