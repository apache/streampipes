## Part of Speech Tagger

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Takes in a stream of tokens and marks each token with a part-of-speech tag
The list of used suffixes can be found [here](https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html)

***

## Required input

A stream with a list property which contains the tokens.

***

## Configuration

Simply assign the correct output of the previous stream to the part of speech detector input.

## Output

Appends two list properties to the stream:
1. String list: The tag for each token
2. Double list: The confidence for each tag that it is indeed the given tag (between 0 and 1)

**Example:**

Input: `(tokens: ["Hi", "Joe"])`

Output: `(tokens: ["Hi", "Joe"], tags: ["UH", "NNP"], confidence: [0.82, 0.87])`
