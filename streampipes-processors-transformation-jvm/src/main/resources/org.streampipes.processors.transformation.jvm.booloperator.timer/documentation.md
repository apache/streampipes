## Value Changed

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor inverts the value of a boolean field. A TRUE is changed to a FALSE and a FALSE is changed to a TRUE.


***

## Required input

A boolean value is required in the data stream and can be selected with the field mapping.

### Invert Field

The boolean value to be inverted.

***

## Configuration

No further configuration is required

## Output

The event schema does not change, just the value of the selected field is changed. For each event a result event is emitted.