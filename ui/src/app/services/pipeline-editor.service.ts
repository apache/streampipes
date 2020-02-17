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

export class PipelineEditorService {

    JsplumbBridge: any;

    constructor(JsplumbBridge) {
        this.JsplumbBridge = JsplumbBridge;
    }

    getCoordinates(ui, currentZoomLevel) {

        var newLeft = this.getDropPositionX(ui.helper, currentZoomLevel);
        var newTop = this.getDropPositionY(ui.helper, currentZoomLevel);
        return {
            'x': newLeft,
            'y': newTop
        };
    }

    isConnected(element) {
        if (this.JsplumbBridge.getConnections({source: element}).length < 1 && this.JsplumbBridge.getConnections({target: element}).length < 1) {
            return false;
        }
        return true;
    }

    isFullyConnected(json) {
        return json.payload.inputStreams == null ||
            this.JsplumbBridge.getConnections({target: $("#" +json.payload.DOM)}).length == json.payload.inputStreams.length;
    }

    getDropPositionY(helper, currentZoomLevel) {
        var newTop;
        var helperPos = helper.offset();
        var divPos = $('#assembly').offset();
        newTop = (helperPos.top - divPos.top) + (1 - currentZoomLevel) * ((helperPos.top - divPos.top) * 2);
        return newTop;
    }

    getDropPositionX(helper, currentZoomLevel) {
        var newLeft;
        var helperPos = helper.offset();
        var divPos = $('#assembly').offset();
        newLeft = (helperPos.left - divPos.left) + (1 - currentZoomLevel) * ((helperPos.left - divPos.left) * 2);
        return newLeft;
    }

}

PipelineEditorService.$inject = ['JsplumbBridge'];