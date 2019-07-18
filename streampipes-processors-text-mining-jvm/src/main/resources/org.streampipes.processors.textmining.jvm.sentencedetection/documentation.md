## Sentence Detection

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Detects sentences in a text and splits the text accordingly. Only works with english sentences.

***

## Required input

A stream with a string property which contains a text.

***

## Configuration

Simply assign the correct output of the previous stream to the tokenizer input.

## Output

Creates for each sentence in a text a new event in which it replaces the text with the sentence.

**Example:**

Input: `(text: "Hi, how are you? I am fine!")`

Output: `(text: "Hi, how are you?")`, `(text: "I am fine!")`
