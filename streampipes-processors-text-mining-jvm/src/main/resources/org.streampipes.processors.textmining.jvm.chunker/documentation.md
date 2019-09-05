## Chunker

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Segments given tokens into chunks (e.g. noun groups, verb groups, ...) and appends the found chunks to the stream.

***

## Required input

Needs a stream with two string list properties:
1. A list of tokens 
2. A list of part-of-speech tags (the Part-of-Speech processing element can be used for that)

***

## Configuration

Assign the tokens and the part of speech tags to the corresponding stream property.

## Output

**Example:**

Input:
```
tokens: ["John", "is", "a", "Person"]
tags: ["NNP", "VBZ", "DT", "NN"]
```

Output:
```
tokens: ["John", "is", "a", "Person"]
tags: ["NNP", "VBZ", "DT", "NN"]
chunks: ["John", "is", "a Person"]
chunkType: ["NP", "VP", "NP"])
```
