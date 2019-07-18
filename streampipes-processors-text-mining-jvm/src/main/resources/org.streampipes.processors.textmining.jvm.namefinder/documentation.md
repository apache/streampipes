## Name finder

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Loads a trained model which finds names like locations or organizations.

A list of trained models can be found [here](http://opennlp.sourceforge.net/models-1.5/).\
A guide on how to train a new model can be found [here](https://opennlp.apache.org/docs/1.9.1/manual/opennlp.html#tools.namefind.training).

***

## Required input

A stream with a list of tokens from a text.

***

## Configuration

Configure the Name finder so that the tokens are assigned to the "List of Tokens" property

#### Model parameter

The trained model which should be used to find the names.

## Output

Appends a string list property to the stream which contains all found names.

**Example (with an loaded english person-name-model):**

Input: `(tokens: ["Hi", "John", "Doe", "is", "here"])`

Output: `(tokens: ["Hi", "John", "Doe", "is", "here"], foundNames: ["John Doe"])`


