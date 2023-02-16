/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Injectable } from '@angular/core';
import { LabelingMode } from '../model/labeling-mode';

@Injectable()
export class LabelingModeService {
    private labelingMode: LabelingMode;
    private lastLabelingMode: LabelingMode;

    constructor() {
        this.labelingMode = LabelingMode.ReactLabeling;
        this.lastLabelingMode = LabelingMode.ReactLabeling;
    }

    isReactMode() {
        return this.labelingMode === LabelingMode.ReactLabeling;
    }

    setReactMode() {
        this.labelingMode = LabelingMode.ReactLabeling;
        this.lastLabelingMode = LabelingMode.ReactLabeling;
    }

    isPolygonMode() {
        return this.labelingMode === LabelingMode.PolygonLabeling;
    }

    setPolygonMode() {
        this.labelingMode = LabelingMode.PolygonLabeling;
        this.lastLabelingMode = LabelingMode.PolygonLabeling;
    }

    isBrushMode() {
        return this.labelingMode === LabelingMode.BrushLabeling;
    }

    setBrushMode() {
        this.labelingMode = LabelingMode.BrushLabeling;
        this.lastLabelingMode = LabelingMode.BrushLabeling;
    }

    isNoneMode() {
        return this.labelingMode === LabelingMode.NoneLabeling;
    }

    setNoneMode() {
        this.labelingMode = LabelingMode.NoneLabeling;
    }

    getMode() {
        return this.labelingMode;
    }

    toggleNoneMode() {
        if (this.labelingMode === LabelingMode.NoneLabeling) {
            this.labelingMode = this.lastLabelingMode;
        } else {
            this.labelingMode = LabelingMode.NoneLabeling;
        }
    }
}
