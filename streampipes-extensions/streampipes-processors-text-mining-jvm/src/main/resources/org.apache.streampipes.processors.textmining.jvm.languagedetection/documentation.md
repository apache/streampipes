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

## Language Detection

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Detects the language of incoming text. For a proper detection each text should contain at least 2 sentences.

Supported languages:
* Afrikaans (afr)
* Arabic (ara)
* Asturian (ast)
* Azerbaijani (aze)
* Bashkir (bak)
* Belarusian (bel)
* Bengali (ben)
* Bosnian (bos)
* Breton (bre)
* Bulgarian (bul)
* Catalan (cat)
* Cebuano (ceb)
* Czech (ces)
* Chechen (che)
* Mandarin Chinese (cmn)
* Welsh (cym)
* Danish (dan)
* German (deu)
* Standard Estonian (ekk)
* Greek, Modern (ell)
* English (eng)
* Esperanto (epo)
* Estonian (est)
* Basque (eus)
* Faroese (fao)
* Persian (fas)
* Finnish (fin)
* French (fra)
* Western Frisian (fry)
* Irish (gle)
* Galician (glg)
* Swiss German (gsw)
* Gujarati (guj)
* Hebrew (heb)
* Hindi (hin)
* Croatian (hrv)
* Hungarian (hun)
* Armenian (hye)
* Indonesian (ind)
* Icelandic (isl)
* Italian (ita)
* Javanese (jav)
* Japanese (jpn)
* Kannada (kan)
* Georgian (kat)
* Kazakh (kaz)
* Kirghiz (kir)
* Korean (kor)
* Latin (lat)
* Latvian (lav)
* Limburgan (lim)
* Lithuanian (lit)
* Luxembourgish (ltz)
* Standard Latvian (lvs)
* Malayalam (mal)
* Marathi (mar)
* Minangkabau (min)
* Macedonian (mkd)
* Maltese (mlt)
* Mongolian (mon)
* Maori (mri)
* Malay (msa)
* Min Nan Chinese (nan)
* Low German (nds)
* Nepali (nep)
* Dutch (nld)
* Norwegian Nynorsk (nno)
* Norwegian Bokmål (nob)
* Occitan (oci)
* Panjabi (pan)
* Iranian Persian (pes)
* Plateau Malagasy (plt)
* Western Panjabi (pnb)
* Polish (pol)
* Portuguese (por)
* Pushto (pus)
* Romanian (ron)
* Russian (rus)
* Sanskrit (san)
* Sinhala (sin)
* Slovak (slk)
* Slovenian (slv)
* Somali (som)
* Spanish (spa)
* Albanian (sqi)
* Serbian (srp)
* Sundanese (sun)
* Swahili (swa)
* Swedish (swe)
* Tamil (tam)
* Tatar (tat)
* Telugu (tel)
* Tajik (tgk)
* Tagalog (tgl)
* Thai (tha)
* Turkish (tur)
* Ukrainian (ukr)
* Urdu (urd)
* Uzbek (uzb)
* Vietnamese (vie)
* Volapük (vol)
* Waray (war)
* Zulu (zul)

***

## Required input

A stream with a string property which contains a text.
The longer the text, the higher the accuracy of the language detector.


***

## Configuration

Simply assign the correct output of the previous stream to the language detector input.
To use this component you have to download or train an openNLP model:
https://opennlp.apache.org/models.html

## Output

Adds two fields to the event:
1. String Property: The acronym of the detected language which can be seen above.
2. Double Property: The confidence of the detector that it found the correct language. Between 0 (not certain at all) and 1 (very certain).


**Example:**

Input: `(text: "Hi, how are you?")`

Output: `(text: "Hi, how are you?", language: "eng", confidenceLanguage: 0.89)`
