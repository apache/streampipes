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

import Konva from 'konva';
import {
    HyperlinkConfig,
    SelectedVisualizationData,
} from '../model/selected-visualization-data.model';

@Injectable()
export class ShapeService {
    constructor() {}

    makeNewHyperlinkGroup(hyperlinkConfig: HyperlinkConfig) {
        const group = this.makeGroup(true);
        group.add(this.makeHyperlinkLabel(hyperlinkConfig));
        return group;
    }

    makeHyperlinkLabel(hyperlinkConfig: HyperlinkConfig): Konva.Label {
        const label = new Konva.Label({
            x: 200,
            y: 40,
        });
        label.add(this.makeHyperlinkText(hyperlinkConfig));
        return label;
    }

    makeHyperlinkText(hyperlinkConfig: HyperlinkConfig): Konva.Text {
        const settings: any = {
            text: hyperlinkConfig.linkLabel,
            width: 'auto',
            height: 'auto',
            hyperlink: hyperlinkConfig.linkHref,
            newWindow: hyperlinkConfig.newWindow,
            textDecoration: 'underline',
            fill: '#62e497',
            fontSize: hyperlinkConfig.labelFontSize,
        };
        return new Konva.Text(settings);
    }

    makeNewMeasurementShape(
        visualizationConfig: SelectedVisualizationData,
    ): Konva.Group {
        const visualizationGroup = this.makeGroup(true);
        visualizationGroup.add(this.makeLabelGroup(visualizationConfig));
        visualizationGroup.add(this.makeMeasurementGroup(visualizationConfig));
        return visualizationGroup;
    }

    makeLabelGroup(config: SelectedVisualizationData): Konva.Group {
        const labelGroup = this.makeGroup(false);
        labelGroup.add(
            this.makeRect(config.labelBackgroundColor, 120, 40, 140, 20),
        );
        labelGroup.add(
            this.makeText(
                config,
                config.label,
                config.labelTextColor,
                120,
                45,
                140,
                20,
                false,
            ),
        );
        return labelGroup;
    }

    makeMeasurementGroup(config: SelectedVisualizationData): Konva.Group {
        const measurementGroup = this.makeGroup(false);
        measurementGroup.add(
            this.makeRect(config.measurementBackgroundColor, 120, 60, 140, 40),
        );
        measurementGroup.add(
            this.makeText(
                config,
                config.measurement,
                config.measurementTextColor,
                120,
                65,
                140,
                40,
                true,
            ),
        );
        return measurementGroup;
    }

    makeGroup(draggable: boolean): Konva.Group {
        return new Konva.Group({
            x: 120,
            y: 40,
            draggable,
        });
    }

    makeRect(
        fillColor: string,
        x: number,
        y: number,
        width: number,
        height: number,
    ): Konva.Rect {
        return new Konva.Rect({
            x,
            y,
            fill: fillColor,
            width,
            height,
        });
    }

    makeText(
        config: SelectedVisualizationData,
        text: string,
        textColor: string,
        x: number,
        y: number,
        width: number | string,
        height: number | string,
        dynamicContent: boolean,
    ): Konva.Text {
        const textSettings: any = {
            text,
            x,
            y,
            width,
            height,
            fill: textColor,
            align: 'center',
            fontSize: '15',
        };

        if (dynamicContent) {
            textSettings.name = 'dynamic-text';
            textSettings.dataLakeMeasure = config.dataLakeMeasure;
            textSettings.fontSize = '30';
        }

        return new Konva.Text(textSettings);
    }
}
