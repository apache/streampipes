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
import { SpecificAdapterInput } from '../model/SpecificAdapterInput';
import { UserInputType } from '../model/UserInputType';

export class SpecificAdapterBuilder {
    specificAdapterInput: SpecificAdapterInput;

    constructor(type: string) {
        this.specificAdapterInput = new SpecificAdapterInput();
        this.specificAdapterInput.adapterType = type;
        this.specificAdapterInput.adapterConfiguration = [];
    }

    public static create(name: string) {
        return new SpecificAdapterBuilder(name);
    }

    public setName(name: string) {
        this.specificAdapterInput.adapterName = name;
        return this;
    }

    public setTimestampProperty(timestsmpProperty: string) {
        this.specificAdapterInput.timestampProperty = timestsmpProperty;
        return this;
    }

    public setStoreInDataLake() {
        this.specificAdapterInput.storeInDataLake = true;
        return this;
    }

    public setStartAdapter(startAdapter: boolean) {
        this.specificAdapterInput.startAdapter = startAdapter;
        return this;
    }

    public withAutoAddedTimestamp() {
        this.specificAdapterInput.autoAddTimestamp = true;
        return this;
    }

    public addInput(type: UserInputType, selector: string, value?: string) {
        const userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;

        this.specificAdapterInput.adapterConfiguration.push(userInput);

        return this;
    }

    build() {
        return this.specificAdapterInput;
    }
}
