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

export class SepaStreamDetailController {

    activeStreamTab: any;

    constructor() {
        this.activeStreamTab = "basics";
    }
    
    selectStreamTab(name) {
        this.activeStreamTab = name;
    }

    isStreamTabSelected(name) {
        return this.activeStreamTab == name;
    }

    getStreamActiveTabCss(name) {
        if (name == this.activeStreamTab) return "md-fab md-accent md-mini";
        else return "md-fab md-accent md-mini wizard-inactive";
    }

    addProperty(properties) {
        properties.push({
            "type": "org.streampipes.model.schema.EventPropertyPrimitive",
            "properties": {"runtimeName": "", "runtimeType": "", "domainProperties": []}
        });
    }

    removeProperty(index, properties) {
        properties.splice(index, 1);
    }
}