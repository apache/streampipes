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

import { UserInput } from '../model/UserInput';
import { UserInputType } from '../model/UserInputType';
import { AdapterInput } from '../model/AdapterInput';

export class AdapterBuilder {
    adapterInput: AdapterInput;

    constructor(type: string) {
        this.adapterInput = new AdapterInput();
        this.adapterInput.adapterType = type;
        this.adapterInput.adapterConfiguration = [];
        this.adapterInput.formatConfiguration = [];
    }

    public static create(name: string) {
        return new AdapterBuilder(name);
    }

    public setName(name: string) {
        this.adapterInput.adapterName = name;
        return this;
    }

    public setTimestampProperty(timestsmpProperty: string) {
        this.adapterInput.timestampProperty = timestsmpProperty;
        return this;
    }

    public addDimensionProperty(dimensionPropertyName: string) {
        this.adapterInput.dimensionProperties.push(dimensionPropertyName);
        return this;
    }

    public setStoreInDataLake() {
        this.adapterInput.storeInDataLake = true;
        return this;
    }

    public addInput(type: UserInputType, selector: string, value?: string) {
        const userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;

        this.adapterInput.adapterConfiguration.push(userInput);

        return this;
    }

    public setStartAdapter(startAdapter: boolean) {
        this.adapterInput.startAdapter = startAdapter;
        return this;
    }

    public addProtocolInput(
        type: UserInputType,
        selector: string,
        value: string,
    ) {
        const userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;

        this.adapterInput.adapterConfiguration.push(userInput);

        return this;
    }

    public setFormat(
        format: 'csv' | 'json' | 'json_array' | 'json_object' | 'xml',
    ) {
        this.adapterInput.format = format;
        return this;
    }

    public addFormatInput(
        type: UserInputType,
        selector: string,
        value: string,
    ) {
        const userInput = new UserInput();
        userInput.type = type;
        userInput.selector = this.escapeString(selector);
        userInput.value = value;

        this.adapterInput.formatConfiguration.push(userInput);

        return this;
    }

    build() {
        return this.adapterInput;
    }

    escapeString(str: string): string {
        return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }
}
