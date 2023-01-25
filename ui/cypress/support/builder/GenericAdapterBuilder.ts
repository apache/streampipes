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
import { GenericAdapterInput } from '../model/GenericAdapterInput';
import { UserInputType } from '../model/UserInputType';

export class GenericAdapterBuilder {
    genericAdapterInput: GenericAdapterInput;

    constructor(type: string) {
        this.genericAdapterInput = new GenericAdapterInput();
        this.genericAdapterInput.adapterType = type;
        this.genericAdapterInput.protocolConfiguration = [];
        this.genericAdapterInput.formatConfiguration = [];
    }

    public static create(name: string) {
        return new GenericAdapterBuilder(name);
    }

    public setName(name: string) {
        this.genericAdapterInput.adapterName = name;
        return this;
    }

    public setTimestampProperty(timestsmpProperty: string) {
        this.genericAdapterInput.timestampProperty = timestsmpProperty;
        return this;
    }

    public addDimensionProperty(dimensionPropertyName: string) {
        this.genericAdapterInput.dimensionProperties.push(
            dimensionPropertyName,
        );
        return this;
    }

    public setStoreInDataLake() {
        this.genericAdapterInput.storeInDataLake = true;
        return this;
    }

    public setStartAdapter(startAdapter: boolean) {
        this.genericAdapterInput.startAdapter = startAdapter;
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

        this.genericAdapterInput.protocolConfiguration.push(userInput);

        return this;
    }

    public setFormat(format: 'csv' | 'json_array' | 'json_object') {
        this.genericAdapterInput.format = format;
        return this;
    }

    public addFormatInput(
        type: UserInputType,
        selector: string,
        value: string,
    ) {
        const userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;

        this.genericAdapterInput.formatConfiguration.push(userInput);

        return this;
    }

    build() {
        return this.genericAdapterInput;
    }
}
