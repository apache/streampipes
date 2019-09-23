/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Injectable} from "@angular/core";


import Konva from "konva";
import {SelectedVisualizationData} from "../model/selected-visualization-data.model";

@Injectable()
export class ShapeService {

    constructor() {

    }

    makeNewMeasurementShape(visualizationConfig: SelectedVisualizationData): Konva.Group {
        let visualizationGroup = this.makeGroup(true);
        visualizationGroup.add(this.makeLabelGroup(visualizationConfig));
        visualizationGroup.add(this.makeMeasurementGroup(visualizationConfig));
        return visualizationGroup;
    }

    makeLabelGroup(config: SelectedVisualizationData): Konva.Group {
        let labelGroup = this.makeGroup(false);
        labelGroup.add(this.makeRect(config.labelBackgroundColor, 120, 40, 120, 20));
        labelGroup.add(this.makeText(config, config.label, config.labelTextColor, 120, 45, 120, 20, false))
        return labelGroup;
    }

    makeMeasurementGroup(config: SelectedVisualizationData): Konva.Group {
        let measurementGroup = this.makeGroup(false);
        measurementGroup.add(this.makeRect(config.measurementBackgroundColor, 120, 60, 120, 40));
        measurementGroup.add(this.makeText(config, config.measurement, config.measurementTextColor, 120, 65, 120, 40, true))
        return measurementGroup;
    }

    makeGroup(draggable: boolean): Konva.Group {
        return new Konva.Group({
            x: 120,
            y: 40,
            draggable: draggable
        });
    }

    makeRect(fillColor: string, x: number, y: number, width: number, height: number): Konva.Rect {
        return new Konva.Rect({
            x: x,
            y: y,
            fill: fillColor,
            width: width,
            height: height,
        });
    }

    makeText(config: SelectedVisualizationData, text: string, textColor: string, x: number, y: number, width: number, height: number, dynamicContent: boolean): Konva.Text {
        let textSettings: any = {
            text: text,
            x: x,
            y: y,
            width: width,
            height: height,
            fill: textColor,
            align: 'center',
            fontSize: '15'
        };

        if (dynamicContent) {
            textSettings.name = "dynamic-text";
            textSettings.brokerUrl = config.brokerUrl;
            textSettings.topic = config.topic;
            textSettings.fontSize = '30';
        }

        return new Konva.Text(textSettings);
    }
}
