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

export class PropertySelectionController {

    outputStrategy: any;
    eventProperty: any;
    layer: any;

    toggle(runtimeId) {
        if (this.exists(runtimeId)) {
            this.remove(runtimeId);
        } else {
            this.add(runtimeId);
        }
    }

    exists(runtimeId) {
        return this.outputStrategy.properties.selectedPropertyKeys.some(e => e === runtimeId);
    }

    add(runtimeId) {
        this.outputStrategy.properties.selectedPropertyKeys.push(runtimeId);
        // This is needed to trigger update of scope
        this.outputStrategy.properties.selectedPropertyKeys = this.outputStrategy.properties.selectedPropertyKeys.filter(el => {return true;});
    }

    remove(runtimeId) {
        this.outputStrategy.properties.selectedPropertyKeys =  this.outputStrategy.properties.selectedPropertyKeys.filter(el => { return el != runtimeId; });
    }
}