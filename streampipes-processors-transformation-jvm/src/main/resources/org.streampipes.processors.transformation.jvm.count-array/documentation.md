## Count Array

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor takes a list field, computes the size of the list and appends the result to the event.

***

## Required input

This processor works with any event that has a field of type ``list``.

***

## Configuration

Describe the configuration parameters here

### List Field

The field containing the list that should be used.

## Output

Outputs the incoming event while appending the list size (named ``countValue``) to the incoming event.