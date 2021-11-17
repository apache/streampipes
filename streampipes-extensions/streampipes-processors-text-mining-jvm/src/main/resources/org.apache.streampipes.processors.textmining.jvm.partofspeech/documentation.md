<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

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
To use this component you have to download or train an openNLP model:
https://opennlp.apache.org/models.html

## Output

Appends two list properties to the stream:
1. String list: The tag for each token
2. Double list: The confidence for each tag that it is indeed the given tag (between 0 and 1)

**Example:**

Input: `(tokens: ["Hi", "Joe"])`

Output: `(tokens: ["Hi", "Joe"], tags: ["UH", "NNP"], confidence: [0.82, 0.87])`
