/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { Vocabulary } from './vocabulary';

export class Datatypes {
    static readonly Integer = new Datatypes(Vocabulary.XSD, 'integer');
    static readonly Long = new Datatypes(Vocabulary.XSD, 'long');
    static readonly Float = new Datatypes(Vocabulary.XSD, 'float');
    static readonly Boolean = new Datatypes(Vocabulary.XSD, 'boolean');
    static readonly String = new Datatypes(Vocabulary.XSD, 'string');
    static readonly Double = new Datatypes(Vocabulary.XSD, 'double');
    static readonly Number = new Datatypes(Vocabulary.SO, 'Number');

    private constructor(
        private readonly vocabulary: string,
        private readonly entity: string,
    ) {}

    toUri() {
        return this.vocabulary + this.entity;
    }
}
