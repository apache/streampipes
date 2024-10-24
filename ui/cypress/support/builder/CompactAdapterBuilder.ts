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

import { CompactAdapter } from '../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';

export class CompactAdapterBuilder {
    private compactAdapter: CompactAdapter;

    constructor() {
        this.compactAdapter = new CompactAdapter();
        this.compactAdapter.configuration = [];
        this.compactAdapter.createOptions = {
            persist: false,
            start: false,
        };
        this.compactAdapter.transform = {
            rename: {},
            measurementUnit: {},
        };
    }

    public static create(appId: string) {
        const builder = new CompactAdapterBuilder();
        builder.compactAdapter.appId = appId;
        return builder;
    }

    // Optional parameter, when not set a random id will be generated
    public setId(id: string) {
        this.compactAdapter.id = id;
        return this;
    }

    public setName(name: string) {
        this.compactAdapter.name = name;
        return this;
    }

    public setDescription(description: string) {
        this.compactAdapter.description = description;
        return this;
    }

    public withRename(from: string, to: string) {
        this.compactAdapter.transform.rename[from] = to;
        return this;
    }

    public addConfiguration(key: string, value: string) {
        const configuration = { [key]: value };
        this.compactAdapter.configuration.push(configuration);
        return this;
    }

    public setPersist() {
        this.compactAdapter.createOptions.persist = true;
        return this;
    }

    public setStart() {
        this.compactAdapter.createOptions.start = true;
        return this;
    }

    public build() {
        if (!this.compactAdapter.id) {
            this.compactAdapter.id =
                'sp:adapterdescription:' +
                Math.random().toString(36).substring(7);
        }
        return this.compactAdapter;
    }
}
