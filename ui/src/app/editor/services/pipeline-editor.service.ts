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

import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PipelineEditorService {
    constructor() {}

    getCoordinates(ui, currentZoomLevel) {
        const newLeft = this.getDropPositionX(ui.helper, currentZoomLevel);
        const newTop = this.getDropPositionY(ui.helper, currentZoomLevel);
        return {
            x: newLeft,
            y: newTop,
        };
    }

    getDropPositionY(helper, currentZoomLevel) {
        const helperPos = helper.offset();
        const divPos = this.getDivPos();
        return (
            helperPos.top -
            divPos.top +
            (1 - currentZoomLevel) * ((helperPos.top - divPos.top) * 2)
        );
    }

    getDropPositionX(helper, currentZoomLevel) {
        const helperPos = helper.offset();
        const divPos = this.getDivPos();
        return (
            helperPos.left -
            divPos.left +
            (1 - currentZoomLevel) * ((helperPos.left - divPos.left) * 2)
        );
    }

    getDivPos() {
        return $('#assembly').offset();
    }
}
