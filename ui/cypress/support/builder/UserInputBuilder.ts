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

import { UserInput } from '../model/UserInput';
import { UserInputType } from '../model/UserInputType';

export class UserInputBuilder {
    userInputs: UserInput[];

    constructor() {
        this.userInputs = [];
    }

    public static create(): UserInputBuilder {
        return new UserInputBuilder();
    }

    public add(
        type: UserInputType,
        selector: string,
        value?: string,
    ): UserInputBuilder {
        const userInput = new UserInput();
        userInput.type = type;
        userInput.selector = selector;
        userInput.value = value;
        this.userInputs.push(userInput);
        return this;
    }

    build(): UserInput[] {
        return this.userInputs;
    }
}
