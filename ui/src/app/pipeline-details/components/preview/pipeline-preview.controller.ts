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

export class PipelinePreviewController {

    PipelinePositioningService: any;
    JsplumbService: any;
    $timeout: any;
    ObjectProvider: any;
    JsplumbBridge: any;
    jspcanvas: any;
    rawPipelineModel: any;
    pipeline: any;
    selectedElement: any;

    constructor(PipelinePositioningService, JsplumbService, $timeout, ObjectProvider, JsplumbBridge) {
        this.PipelinePositioningService = PipelinePositioningService;
        this.JsplumbService = JsplumbService;
        this.$timeout = $timeout;
        this.ObjectProvider = ObjectProvider;
        this.JsplumbBridge = JsplumbBridge;
    }

    $onInit() {
        this.$timeout(() => {
            var elid = "#" + this.jspcanvas;
            this.rawPipelineModel = this.JsplumbService.makeRawPipeline(this.pipeline, true);
            this.$timeout(() => {
                this.PipelinePositioningService.displayPipeline(this.rawPipelineModel, elid, true, true);
                var existingEndpointIds = [];
                this.$timeout(() => {
                    this.JsplumbBridge.selectEndpoints().each(endpoint => {
                        if (existingEndpointIds.indexOf(endpoint.element.id) === -1) {
                            $(endpoint.element).click(() => {
                                var payload = this.ObjectProvider.findElement(endpoint.element.id, this.rawPipelineModel).payload;
                                this.selectedElement = payload;
                            });
                            existingEndpointIds.push(endpoint.element.id);
                        }
                    });
                });
            });
        });
    }
}

PipelinePreviewController.$inject = ['PipelinePositioningService', 'JsplumbService', '$timeout', 'ObjectProvider', 'JsplumbBridge'];