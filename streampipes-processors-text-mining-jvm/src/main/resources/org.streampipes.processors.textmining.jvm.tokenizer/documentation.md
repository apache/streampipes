## Tokenizer

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Segments a given text into Tokens (usually words, numbers, punctuations, ...). Works best with english text.

***

## Required input

A stream with a string property which contains a text.

***

## Configuration

Simply assign the correct output of the previous stream to the tokenizer input.

## Output

Adds a list to the stream which contains all tokens of the corresponding text.

**Example:**

Input: `(text: "Hi, how are you?")`

Output: `(text: "Hi, how are you?", tokens: ["Hi", ",", "how", "are", "you", "?"])`

