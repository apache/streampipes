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

import * as angular from 'angular';

export class OutputStrategyController {

    outputStrategyTypes: any;
    selectedOutputStrategy: any;
    strategies: any;

    constructor() {
        this.outputStrategyTypes = [{label: "Append", "type": "org.apache.streampipes.model.output.AppendOutputStrategy"},
            {label: "Custom", "type": "org.apache.streampipes.model.output.CustomOutputStrategy"},
            {label: "Fixed", "type": "org.apache.streampipes.model.output.FixedOutputStrategy"},
            {label: "List", "type": "org.apache.streampipes.model.output.ListOutputStrategy"},
            {label: "Keep", "type": "org.apache.streampipes.model.output.RenameOutputStrategy"}];

        this.selectedOutputStrategy = this.outputStrategyTypes[0].type;
    }

    addOutputStrategy(strategies) {
        if (strategies == undefined) this.strategies = [];
        this.strategies.push(this.getNewOutputStrategy());
    }

    removeOutputStrategy(strategies, index) {
        strategies.splice(index, 1);
    }

    getNewOutputStrategy() {
        if (this.selectedOutputStrategy === this.outputStrategyTypes[0].type)
            return {"type": this.outputStrategyTypes[0].type, "properties": {"eventProperties": []}};
        else if (this.selectedOutputStrategy === this.outputStrategyTypes[1].type)
            return {"type": this.outputStrategyTypes[1].type, "properties": {"eventProperties": []}};
        else if (this.selectedOutputStrategy === this.outputStrategyTypes[2].type)
            return {"type": this.outputStrategyTypes[2].type, "properties": {"eventProperties": []}};
        else if (this.selectedOutputStrategy === this.outputStrategyTypes[3].type)
            return {"type": this.outputStrategyTypes[3].type, "properties": {}};
        else if (this.selectedOutputStrategy === this.outputStrategyTypes[4].type)
            return {"type": this.outputStrategyTypes[4].type, "properties": {}};

    }

    getType(strategy) {
        var label;
        angular.forEach(this.outputStrategyTypes, function (value) {
            if (value.type == strategy.type) label = value.label;
        });
        return label;
    }
}