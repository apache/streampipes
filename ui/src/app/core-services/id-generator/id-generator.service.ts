/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
import { Injectable } from '@angular/core';

@Injectable()
export class IdGeneratorService {
    private possibleCharacters =
        '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';

    private idPrefix = 'urn:streampipes.org:spi:';

    /**
     * Generates a random id with the given length
     */
    public generate(length: number): string {
        let result = '';
        for (let i = length; i > 0; --i) {
            result +=
                this.possibleCharacters[
                    Math.round(
                        Math.random() * (this.possibleCharacters.length - 1),
                    )
                ];
        }
        return result;
    }

    /**
     * Generates a random id with a fix prefix
     */
    public generatePrefixedId(): string {
        return this.idPrefix + this.generate(6);
    }
}
